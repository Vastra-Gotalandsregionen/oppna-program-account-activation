package se.vgregion.account.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import se.vgregion.account.services.repository.ExternalUserStructureRepository;
import se.vgregion.account.services.util.StructureQueryUtil;
import se.vgregion.create.domain.ExternalUserStructure;

import java.util.*;

public class StructureServiceImpl implements StructureService {

    @Autowired
    private ExternalUserStructureRepository structureRepository;

    @Value("${max.results:10}")
    private int maxResults;

    @Autowired
    private StructureQueryUtil queryUtil;

    @Override
    public Collection<String> search(String query) {
        Set<String> replyList = new TreeSet<String>();

        if (query == null) {
            query = "";
        }
        query = query.toLowerCase();
        String[] queryParts = query.split("/");
        queryParts[queryParts.length - 1] += "%";

        String joinWhereClause = queryUtil.whereClause(queryParts, true);

        List<ExternalUserStructure> result = (List<ExternalUserStructure>) structureRepository
                .findByQuery("select s from ExternalUserStructure s " + joinWhereClause, queryParts);

        int cnt = 0;
        outer:
        for (ExternalUserStructure externalUserStructure : result) {
            String base = queryUtil.resolveBase(externalUserStructure);
            List<ExternalUserStructure> children = (List<ExternalUserStructure>) structureRepository
                    .findByQuery("select s from ExternalUserStructure s join s.parent s2 where s2 = ?1",
                            new Object[]{externalUserStructure});
            if (replyList.add(base)) cnt++;
            if (cnt >= maxResults) break;
            for (ExternalUserStructure child : children) {
                if (replyList.add(base + "/" + child.getName())) cnt++;
                if (cnt >= maxResults) break outer;
            }
        }

        return replyList;
    }

    @Override
    @Transactional
    public void storeExternStructurePersonDn(String externStructurePersonDn) {
        String[] parts = externStructurePersonDn.split("/");

        // iterate through parts until an existing parent is found
        ExternalUserStructure parent = null;
        int i = parts.length;
        while (i > 0) {
            parent = existingStructure(Arrays.copyOf(parts, i));
            if (parent != null) break; // existing parent found
            i--;
        }

        // create the children that were not found
        for (int j=i; j < parts.length; j++) {
            ExternalUserStructure child = new ExternalUserStructure();
            child.setParent(parent);
            child.setName(parts[j]);

            structureRepository.persist(child);

            parent = child;
        }
    }

    private ExternalUserStructure existingStructure(String[] parts) {
        String where = queryUtil.whereClause(parts, false);
        List<ExternalUserStructure> result = (List<ExternalUserStructure>) structureRepository
                .findByQuery("select s from ExternalUserStructure s " + where, parts);

        if (result.size() == 1) {
            return result.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
}

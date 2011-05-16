package se.vgregion.account.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import se.vgregion.account.services.repository.ExternalUserStructureRepository;
import se.vgregion.account.services.util.StructureQueryUtil;
import se.vgregion.create.domain.ExternalUserStructure;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class StructureService {

    @Autowired
    private ExternalUserStructureRepository structureRepository;

    @Value("${max.results:10}")
    private int maxResults;

    @Autowired
    private StructureQueryUtil queryUtil;

    public Set<String> search(String query) {
        Set<String> replyList = new TreeSet<String>();

        if (query == null) {
            query = "";
        }
        query = query.toLowerCase();
        String[] queryParts = query.split("/");
        queryParts[queryParts.length - 1] += "%";

        String joinWhereClause = queryUtil.whereClause(queryParts);

        List<ExternalUserStructure> result = (List<ExternalUserStructure>) structureRepository
                .findByQuery("select s from ExternalUserStructure s " + joinWhereClause, queryParts);

        int cnt = 0;
        outer:
        for (ExternalUserStructure externalUserStructure : result) {
            String base = queryUtil.resolveBase(externalUserStructure);
            List<ExternalUserStructure> children = (List<ExternalUserStructure>) structureRepository
                    .findByQuery("select s from ExternalUserStructure s join s.parent s2 where s2 = ?1",
                            new Object[]{externalUserStructure});
            if(replyList.add(base)) cnt++;
            if (cnt >= maxResults) break;
            for (ExternalUserStructure child : children) {
                if (replyList.add(base + "/" + child.getName())) cnt++;
                if (cnt >= maxResults) break outer;
            }
        }

        return replyList;
    }
}

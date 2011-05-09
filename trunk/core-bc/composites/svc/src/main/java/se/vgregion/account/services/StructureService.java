package se.vgregion.account.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import se.vgregion.account.services.util.StructureQueryUtil;
import se.vgregion.create.domain.ExternalUserStructure;
import se.vgregion.dao.domain.patterns.repository.db.jpa.JpaRepository;

import java.util.ArrayList;
import java.util.List;

public class StructureService {
    @Autowired
    @Qualifier(value = "externalUserStructureRepository")
    private JpaRepository<ExternalUserStructure, Long, Long> structureRepository;

    @Autowired
    private StructureQueryUtil queryUtil;

    public List<String> search(String query) {
        List<String> replyList = new ArrayList<String>();

        String[] queryParts = query.split("/");
        queryParts[queryParts.length - 1] += "%";

        String whereClauseStart = queryUtil.whereClause(queryParts);

        List<ExternalUserStructure> result = (List<ExternalUserStructure>) structureRepository
                .findByQuery("select s from ExternalUserStructure s " + whereClauseStart, queryParts);

        int cnt = 0;
        outer:
        for (ExternalUserStructure externalUserStructure : result) {
            String base = queryUtil.resolveBase(externalUserStructure);
            List<ExternalUserStructure> children = (List<ExternalUserStructure>) structureRepository
                    .findByQuery("select s from ExternalUserStructure s where s.parent = ?1",
                            new Object[]{externalUserStructure});
            for (ExternalUserStructure child : children) {
                if (cnt >= 10) break outer;
                replyList.add(base + "/" + child.getName());
                cnt++;
            }
        }

        return replyList;
    }
}

package org.demkiv.domain.util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FindmeUtil {

    public static Set<Long> randomIds(List<Long> ids, int count) {
        Set<Long> personIds = new HashSet<>();

        while (personIds.size() < count && personIds.size() < ids.size()) {
            int randomIndex = getRandomDiceNumber(ids.size());
            personIds.add(ids.get(randomIndex));
        }

        return personIds;
    }

    private static int getRandomDiceNumber(int count)
    {
        return (int) (Math.random() * count);
    }
}

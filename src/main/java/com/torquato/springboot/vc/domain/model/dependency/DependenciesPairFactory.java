package com.torquato.springboot.vc.domain.model.dependency;

import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
public class DependenciesPairFactory {

    public List<DependenciesPair> createAscPairs(final List<Dependencies> dependencies) {
        final List<Dependencies> dependenciesCopy = new ArrayList<>(dependencies);
        Collections.sort(dependenciesCopy);

        final List<DependenciesPair> pairs = new LinkedList<>();
        for (int i = 0; i < dependenciesCopy.size(); i++) {
            final Dependencies left = dependenciesCopy.get(i);
            for (int j = i + 1; j < dependenciesCopy.size(); j++) {
                final Dependencies right = dependenciesCopy.get(j);
                pairs.add(new DependenciesPair(left, right));
            }
        }
        return pairs;
    }

}

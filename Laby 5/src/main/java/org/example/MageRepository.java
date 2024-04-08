package org.example;
import java.util.*;
public class MageRepository {
    private Collection<Mage> collection;

    public MageRepository() {
        this.collection = new ArrayList<>();
    }

    public Optional<Mage> find(String name) {
        for (Mage mage : collection) {
            if ( mage.getName().equals(name)) {
                return Optional.of(mage);
            }
        }
        return Optional.empty();
    }

    public void delete(String name) {
        for (Mage mage : collection) {
            if (mage.getName().equals(name)) {
                collection.remove(mage);
                return;
            }
        }
        throw new IllegalArgumentException("Mage not found");
    }

    public void save(Mage mage) {
        for (Mage mage_ : collection) {
            if (mage_.getName().equals(mage.getName())) {
                throw new IllegalArgumentException("Mage already exists");
            }
        }
        collection.add(mage);
    }
}

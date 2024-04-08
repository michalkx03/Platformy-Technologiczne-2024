package org.example;
import java.util.*;
public class MageController {
    private final MageRepository rep;
    public MageController(MageRepository rep) {
        this.rep = rep;
    }
    public String find(String name) {
        Optional<Mage> mage = rep.find(name);
        if (mage.isPresent()) {
            return mage.get().toString();
        } else {
            return "not found";
        }
    }
    public String delete(String name) {
        try {
            rep.delete(name);
            return "done";
        } catch (IllegalArgumentException e) {
            return "not found";
        }
    }
    public String save(String name, int level) {
        try {
            rep.save(new Mage(name, level));
            return "done";
        } catch (IllegalArgumentException e) {
            return "bad request";
        }
    }
}

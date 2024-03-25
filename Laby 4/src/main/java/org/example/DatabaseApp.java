package org.example;

import jakarta.persistence.*;
import jakarta.transaction.Transaction;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

@Entity
class Mage {
    @Id
    private String name;
    private int level;

    @ManyToOne
    private Tower tower;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Tower getTower() {
        return tower;
    }

    public void setTower(Tower tower) {
        this.tower = tower;
        this.tower.addMage(this);
    }
}

@Entity
class Tower {
    @Id
    private String name;
    private int height;

    @OneToMany(mappedBy = "tower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mage> mages = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public List<Mage> getMages() {
        return mages;
    }

    public void addMage(Mage mage){
        mages.add(mage);
    }
    public void removeMage(Mage mage){
        mages.remove(mage);
    }

}


public class DatabaseApp {

    private static EntityManagerFactory entityManagerFactory;

    public static void main(String[] args) {
        entityManagerFactory = Persistence.createEntityManagerFactory("database-app");
        EntityManager entityManager = entityManagerFactory.createEntityManager();

        entityManager.getTransaction().begin();

        Tower tower1 = new Tower();
        tower1.setName("Tower1");
        tower1.setHeight(100);
        Tower tower2 = new Tower();
        tower2.setName("Tower2");
        tower2.setHeight(200);
        Tower tower3 = new Tower();
        tower3.setName("Tower3");
        tower3.setHeight(300);

        Mage mage1 = new Mage();
        mage1.setName("Mage1");
        mage1.setLevel(10);
        mage1.setTower(tower1);

        Mage mage2 = new Mage();
        mage2.setName("Mage2");
        mage2.setLevel(20);
        mage2.setTower(tower2);

        Mage mage3 = new Mage();
        mage3.setName("Mage3");
        mage3.setLevel(30);
        mage3.setTower(tower1);

        entityManager.persist(tower1);
        entityManager.persist(tower2);
        entityManager.persist(tower3);
        entityManager.persist(mage1);
        entityManager.persist(mage2);
        entityManager.persist(mage3);
        entityManager.getTransaction().commit();

        List<Mage> mages11 = entityManager.createQuery("SELECT m FROM Mage m", Mage.class).getResultList();
        for (Mage mage : mages11) {
            System.out.println("Mage: " + mage.getName() + ", Level: " + mage.getLevel());
        }

        entityManager.getTransaction().begin();
        Mage mage22 = entityManager.find(Mage.class, "Mage2");
        mage22.getTower().removeMage(mage22);
        entityManager.remove(mage22);
        entityManager.getTransaction().commit();

        System.out.println("After deletion");

        List<Mage> mages = entityManager.createQuery("SELECT m FROM Mage m", Mage.class).getResultList();
        for (Mage mage : mages) {
            System.out.println("Mage: " + mage.getName() + ", Level: " + mage.getLevel());
        }
        System.out.println("Towers before deletion");
        List<Tower> towers = entityManager.createQuery("SELECT t FROM Tower t", Tower.class).getResultList();
        for (Tower tower : towers) {
            System.out.println("Tower: " + tower.getName() + ", Height: " + tower.getHeight());
        }

        entityManager.getTransaction().begin();

        Tower towerToDelete = entityManager.find(Tower.class, "Tower3");
        entityManager.remove(towerToDelete);

        entityManager.getTransaction().commit();

        System.out.println("After tower deletion");

        List<Tower> towers1 = entityManager.createQuery("SELECT t FROM Tower t", Tower.class).getResultList();
        for (Tower tower : towers1) {
            System.out.println("Tower: " + tower.getName() + ", Height: " + tower.getHeight());
        }

        System.out.println("Zap 1");
        List<Mage> mages1 = entityManager.createQuery("SELECT m FROM Mage m WHERE m.level > :level", Mage.class).setParameter("level", 10).getResultList();
        for (Mage mage : mages1) {
            System.out.println("Mage: " + mage.getName() + ", Level: " + mage.getLevel());
        }
        System.out.println("Zap 2");
        List<Tower> towers2 = entityManager.createQuery("SELECT t FROM Tower t WHERE t.height > :height", Tower.class).setParameter("height", 100).getResultList();
        for (Tower tower : towers2) {
            System.out.println("Tower: " + tower.getName() + ", Height: " + tower.getHeight());
        }

        Tower tower12 = entityManager.find(Tower.class, "Tower1");
        System.out.println("Zap 3");
        List<Mage> mages3 = entityManager.createQuery("SELECT m FROM Mage m WHERE m.level > :level AND m.tower = :tower", Mage.class).setParameter("level", 0).setParameter("tower", tower12).getResultList();
        for (Mage mage : mages3) {
            System.out.println("Mage: " + mage.getName() + ", Level: " + mage.getLevel());
        }

        entityManager.close();
        entityManagerFactory.close();
    }
}

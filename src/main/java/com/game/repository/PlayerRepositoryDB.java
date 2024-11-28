package com.game.repository;

import com.game.entity.Player;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {
    private final SessionFactory sessionFactory;

    public PlayerRepositoryDB() {
        this.sessionFactory = new Configuration().addAnnotatedClass(Player.class).buildSessionFactory();

    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        try (Session session = sessionFactory.openSession()) {
            NativeQuery<Player> nativeQuery = session.createNativeQuery("SELECT * FROM player", Player.class);
            nativeQuery.setFirstResult(pageNumber);
            nativeQuery.setMaxResults(pageSize);
            return nativeQuery.list();
        }
    }

    @Override
    public int getAllCount() {
        try (Session session = sessionFactory.openSession()) {
            Query<Long> countPlayers = session.createNamedQuery("countPlayers", Long.class);
            Long l = countPlayers.uniqueResult();
            return l.intValue();
        }
    }

    @Override
    public Player save(Player player) {
        try (Session session = sessionFactory.openSession()){
            Transaction transaction = session.beginTransaction();
            Long save = (Long)session.save(player);
            session.flush();
            Player player1 = session.get(Player.class, save);
            transaction.commit();
            return player1;
        }
    }

    @Override
    public Player update(Player player) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            Player merge = (Player)session.merge(player);
            session.getTransaction().commit();
            return merge;
        }
    }

    @Override
    public Optional<Player> findById(long id) {
        try (Session session = sessionFactory.openSession()){
            return Optional.ofNullable(session.get(Player.class, id));
        }
    }

    @Override
    public void delete(Player player) {
        try (Session session = sessionFactory.openSession()){
            session.beginTransaction();
            session.delete(player);
            session.getTransaction().commit();
        }

    }

    @PreDestroy
    public void beforeStop() {
    sessionFactory.close();
    }
}
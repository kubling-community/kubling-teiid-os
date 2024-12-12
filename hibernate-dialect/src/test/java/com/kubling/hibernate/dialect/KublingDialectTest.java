package com.kubling.hibernate.dialect;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class KublingDialectTest extends BaseDialectTest {

    @Test
    public void testSimpleInsertSelect() {
        configuration.addAnnotatedClass(Application.class);

        try (Session session = configuration.buildSessionFactory(serviceRegistry).openSession()) {
            session.beginTransaction();
            final var id = UUID.randomUUID().toString();
            session.persist(new Application(id, "Name " + UUID.randomUUID()));
            session.getTransaction().commit();
            Query<Application> query = session.createQuery("from BaseDialectTest.Application where id = :id", Application.class);
            query.setParameter("id", id);

            List<Application> results = query.getResultList();
            assertEquals(1, results.size());

        }
    }

    @Test
    public void testPagination() {
        configuration.addAnnotatedClass(Application.class);

        try (Session session = configuration.buildSessionFactory(serviceRegistry).openSession()) {
            session.beginTransaction();
            for (long i = 1; i <= 10; i++) {
                session.persist(new Application(UUID.randomUUID().toString(), "Name " + i));
            }
            session.getTransaction().commit();

            // Query with pagination
            Query<Application> query = session.createQuery("from BaseDialectTest.Application", Application.class);
            query.setFirstResult(5);
            query.setMaxResults(5);

            List<Application> results = query.getResultList();
            assertEquals(5, results.size());
        }
    }

}

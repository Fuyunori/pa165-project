package tennisclub.dao;

import org.springframework.stereotype.Repository;
import tennisclub.entity.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;


/**
 * @author Ondrej Holub
 */
@Repository
public class UserDaoImpl {

    @PersistenceContext
    EntityManager em;

    public void create(User user) {
        em.persist(user);
    }

    public User findById(Long id) {
        return em.createQuery("select u from User u where u.id=:id", User.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<User> findByName(String name) {
        return em.createQuery("select u from User u where u.name=:name", User.class)
                .setParameter("name", name)
                .getResultList();
    }

    public List<User> findByUsername(String username) {
        return em.createQuery("select u from User u where u.username=:username", User.class)
                .setParameter("username", username)
                .getResultList();
    }

    public List<User> findByEmail(String email) {
        return em.createQuery("select u from User u where u.email=:email", User.class)
                .setParameter("email", email)
                .getResultList();
    }

    public List<User> findAll() {
        return em.createQuery("select u from User u", User.class)
                .getResultList();
    }

    public User update(User user) {
        return em.merge(user);
    }

    public void delete(User user) {
        em.remove(user);
    }
}

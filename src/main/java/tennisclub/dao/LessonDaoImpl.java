package tennisclub.dao;

import org.springframework.stereotype.Repository;
import tennisclub.entity.Lesson;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public class LessonDaoImpl implements LessonDao{
    @PersistenceContext
    private EntityManager em;

    @Override
    public void create(Lesson lesson) {
        em.persist(lesson);
    }

    @Override
    public void update(Lesson lesson) {
        em.merge(lesson);
    }

    @Override
    public void remove(Lesson lesson) {
        if(!em.contains(lesson)){
            em.merge(lesson);
        }
        em.remove(lesson);
    }

    @Override
    public List<Lesson> findAll() {
        return em.createQuery("select l from Lesson l", Lesson.class)
                .getResultList();
    }

    @Override
    public Lesson findById(Long id) {
        return em.createQuery("select l from Lesson l where l.id = :id", Lesson.class)
                .setParameter("id", id)
                .getSingleResult();
    }

/*    @Override
    public List<Lesson> findByLecturerName(String lecturerName) {
        return null;
    }*/

    @Override
    public List<Lesson> findByCapacity(int capacity) {
        return em.createQuery("select l from Lesson l where l.capacity = :capacity", Lesson.class)
                .setParameter("capacity", capacity)
                .getResultList();
    }
}

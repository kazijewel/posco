package database.hibernate;

// Generated Jul 31, 2016 4:54:33 PM by Hibernate Tools 3.4.0.CR1

import java.util.List;
import javax.naming.InitialContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.LockMode;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Example;

/**
 * Home object for domain model class TbEmployeeAttendance.
 * @see database.hibernate.TbEmployeeAttendance
 * @author Hibernate Tools
 */
public class TbEmployeeAttendanceHome {

	private static final Log log = LogFactory
			.getLog(TbEmployeeAttendanceHome.class);

	private final SessionFactory sessionFactory = getSessionFactory();

	protected SessionFactory getSessionFactory() {
		try {
			return (SessionFactory) new InitialContext()
					.lookup("SessionFactory");
		} catch (Exception e) {
			log.error("Could not locate SessionFactory in JNDI", e);
			throw new IllegalStateException(
					"Could not locate SessionFactory in JNDI");
		}
	}

	public void persist(TbEmployeeAttendance transientInstance) {
		log.debug("persisting TbEmployeeAttendance instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(TbEmployeeAttendance instance) {
		log.debug("attaching dirty TbEmployeeAttendance instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TbEmployeeAttendance instance) {
		log.debug("attaching clean TbEmployeeAttendance instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(TbEmployeeAttendance persistentInstance) {
		log.debug("deleting TbEmployeeAttendance instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TbEmployeeAttendance merge(TbEmployeeAttendance detachedInstance) {
		log.debug("merging TbEmployeeAttendance instance");
		try {
			TbEmployeeAttendance result = (TbEmployeeAttendance) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public TbEmployeeAttendance findById(
			database.hibernate.TbEmployeeAttendanceId id) {
		log.debug("getting TbEmployeeAttendance instance with id: " + id);
		try {
			TbEmployeeAttendance instance = (TbEmployeeAttendance) sessionFactory
					.getCurrentSession().get(
							"hibernate.database.TbEmployeeAttendance", id);
			if (instance == null) {
				log.debug("get successful, no instance found");
			} else {
				log.debug("get successful, instance found");
			}
			return instance;
		} catch (RuntimeException re) {
			log.error("get failed", re);
			throw re;
		}
	}

	public List findByExample(TbEmployeeAttendance instance) {
		log.debug("finding TbEmployeeAttendance instance by example");
		try {
			List results = sessionFactory.getCurrentSession()
					.createCriteria("hibernate.database.TbEmployeeAttendance")
					.add(Example.create(instance)).list();
			log.debug("find by example successful, result size: "
					+ results.size());
			return results;
		} catch (RuntimeException re) {
			log.error("find by example failed", re);
			throw re;
		}
	}
}

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
 * Home object for domain model class TemEmployeeInfo.
 * @see database.hibernate.TemEmployeeInfo
 * @author Hibernate Tools
 */
public class TemEmployeeInfoHome {

	private static final Log log = LogFactory.getLog(TemEmployeeInfoHome.class);

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

	public void persist(TemEmployeeInfo transientInstance) {
		log.debug("persisting TemEmployeeInfo instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(TemEmployeeInfo instance) {
		log.debug("attaching dirty TemEmployeeInfo instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TemEmployeeInfo instance) {
		log.debug("attaching clean TemEmployeeInfo instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(TemEmployeeInfo persistentInstance) {
		log.debug("deleting TemEmployeeInfo instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TemEmployeeInfo merge(TemEmployeeInfo detachedInstance) {
		log.debug("merging TemEmployeeInfo instance");
		try {
			TemEmployeeInfo result = (TemEmployeeInfo) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public TemEmployeeInfo findById(database.hibernate.TemEmployeeInfoId id) {
		log.debug("getting TemEmployeeInfo instance with id: " + id);
		try {
			TemEmployeeInfo instance = (TemEmployeeInfo) sessionFactory
					.getCurrentSession().get(
							"hibernate.database.TemEmployeeInfo", id);
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

	public List findByExample(TemEmployeeInfo instance) {
		log.debug("finding TemEmployeeInfo instance by example");
		try {
			List results = sessionFactory.getCurrentSession()
					.createCriteria("hibernate.database.TemEmployeeInfo")
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

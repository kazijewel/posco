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
 * Home object for domain model class TbUdLoginDetails.
 * @see database.hibernate.TbUdLoginDetails
 * @author Hibernate Tools
 */
public class TbUdLoginDetailsHome {

	private static final Log log = LogFactory
			.getLog(TbUdLoginDetailsHome.class);

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

	public void persist(TbUdLoginDetails transientInstance) {
		log.debug("persisting TbUdLoginDetails instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(TbUdLoginDetails instance) {
		log.debug("attaching dirty TbUdLoginDetails instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TbUdLoginDetails instance) {
		log.debug("attaching clean TbUdLoginDetails instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(TbUdLoginDetails persistentInstance) {
		log.debug("deleting TbUdLoginDetails instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TbUdLoginDetails merge(TbUdLoginDetails detachedInstance) {
		log.debug("merging TbUdLoginDetails instance");
		try {
			TbUdLoginDetails result = (TbUdLoginDetails) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public TbUdLoginDetails findById(database.hibernate.TbUdLoginDetailsId id) {
		log.debug("getting TbUdLoginDetails instance with id: " + id);
		try {
			TbUdLoginDetails instance = (TbUdLoginDetails) sessionFactory
					.getCurrentSession().get(
							"hibernate.database.TbUdLoginDetails", id);
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

	public List findByExample(TbUdLoginDetails instance) {
		log.debug("finding TbUdLoginDetails instance by example");
		try {
			List results = sessionFactory.getCurrentSession()
					.createCriteria("hibernate.database.TbUdLoginDetails")
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

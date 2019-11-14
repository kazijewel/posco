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
 * Home object for domain model class TbLeaveBalanceNew.
 * @see database.hibernate.TbLeaveBalanceNew
 * @author Hibernate Tools
 */
public class TbLeaveBalanceNewHome {

	private static final Log log = LogFactory
			.getLog(TbLeaveBalanceNewHome.class);

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

	public void persist(TbLeaveBalanceNew transientInstance) {
		log.debug("persisting TbLeaveBalanceNew instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(TbLeaveBalanceNew instance) {
		log.debug("attaching dirty TbLeaveBalanceNew instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TbLeaveBalanceNew instance) {
		log.debug("attaching clean TbLeaveBalanceNew instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(TbLeaveBalanceNew persistentInstance) {
		log.debug("deleting TbLeaveBalanceNew instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TbLeaveBalanceNew merge(TbLeaveBalanceNew detachedInstance) {
		log.debug("merging TbLeaveBalanceNew instance");
		try {
			TbLeaveBalanceNew result = (TbLeaveBalanceNew) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public TbLeaveBalanceNew findById(database.hibernate.TbLeaveBalanceNewId id) {
		log.debug("getting TbLeaveBalanceNew instance with id: " + id);
		try {
			TbLeaveBalanceNew instance = (TbLeaveBalanceNew) sessionFactory
					.getCurrentSession().get(
							"hibernate.database.TbLeaveBalanceNew", id);
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

	public List findByExample(TbLeaveBalanceNew instance) {
		log.debug("finding TbLeaveBalanceNew instance by example");
		try {
			List results = sessionFactory.getCurrentSession()
					.createCriteria("hibernate.database.TbLeaveBalanceNew")
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

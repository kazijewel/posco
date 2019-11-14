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
 * Home object for domain model class TbFiscalClosingStepStatus.
 * @see database.hibernate.TbFiscalClosingStepStatus
 * @author Hibernate Tools
 */
public class TbFiscalClosingStepStatusHome {

	private static final Log log = LogFactory
			.getLog(TbFiscalClosingStepStatusHome.class);

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

	public void persist(TbFiscalClosingStepStatus transientInstance) {
		log.debug("persisting TbFiscalClosingStepStatus instance");
		try {
			sessionFactory.getCurrentSession().persist(transientInstance);
			log.debug("persist successful");
		} catch (RuntimeException re) {
			log.error("persist failed", re);
			throw re;
		}
	}

	public void attachDirty(TbFiscalClosingStepStatus instance) {
		log.debug("attaching dirty TbFiscalClosingStepStatus instance");
		try {
			sessionFactory.getCurrentSession().saveOrUpdate(instance);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void attachClean(TbFiscalClosingStepStatus instance) {
		log.debug("attaching clean TbFiscalClosingStepStatus instance");
		try {
			sessionFactory.getCurrentSession().lock(instance, LockMode.NONE);
			log.debug("attach successful");
		} catch (RuntimeException re) {
			log.error("attach failed", re);
			throw re;
		}
	}

	public void delete(TbFiscalClosingStepStatus persistentInstance) {
		log.debug("deleting TbFiscalClosingStepStatus instance");
		try {
			sessionFactory.getCurrentSession().delete(persistentInstance);
			log.debug("delete successful");
		} catch (RuntimeException re) {
			log.error("delete failed", re);
			throw re;
		}
	}

	public TbFiscalClosingStepStatus merge(
			TbFiscalClosingStepStatus detachedInstance) {
		log.debug("merging TbFiscalClosingStepStatus instance");
		try {
			TbFiscalClosingStepStatus result = (TbFiscalClosingStepStatus) sessionFactory
					.getCurrentSession().merge(detachedInstance);
			log.debug("merge successful");
			return result;
		} catch (RuntimeException re) {
			log.error("merge failed", re);
			throw re;
		}
	}

	public TbFiscalClosingStepStatus findById(
			database.hibernate.TbFiscalClosingStepStatusId id) {
		log.debug("getting TbFiscalClosingStepStatus instance with id: " + id);
		try {
			TbFiscalClosingStepStatus instance = (TbFiscalClosingStepStatus) sessionFactory
					.getCurrentSession().get(
							"hibernate.database.TbFiscalClosingStepStatus", id);
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

	public List findByExample(TbFiscalClosingStepStatus instance) {
		log.debug("finding TbFiscalClosingStepStatus instance by example");
		try {
			List results = sessionFactory
					.getCurrentSession()
					.createCriteria(
							"hibernate.database.TbFiscalClosingStepStatus")
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

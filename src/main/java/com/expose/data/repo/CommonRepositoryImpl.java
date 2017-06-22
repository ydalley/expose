package com.expose.data.repo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import com.expose.model.IEntity;


public  class CommonRepositoryImpl<T> extends SimpleJpaRepository<T, Serializable>
		implements CommonRepository<T, Serializable> {

	private final EntityManager entityManager;
	private JpaEntityInformation<T, ?> info;

	// private Class<T> type;

	public CommonRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
		super(entityInformation, entityManager);
		this.entityManager = entityManager;
		info = entityInformation;
	}

	@Override
	public Page<T> findUsingPattern(String pattern, Pageable details) {
		String lpattern = pattern.toLowerCase();
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<T> q = (CriteriaQuery<T>) cb.createQuery();
		Root<T> c = q.from(info.getJavaType());
		Predicate[] predicates = null;
		try {
			predicates = new Predicate[getFields().size()];
			int cnt = 0;
			for (String field : getFields()) {
				Predicate predicate = cb.like(cb.lower(c.get(field)), "%" + lpattern + "%");
				predicates[cnt] = predicate;
				cnt++;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			return new PageImpl<>(new ArrayList<>());
		}

		CriteriaQuery<T> baseQuery = null;
		CriteriaQuery<Long> qc = cb.createQuery(Long.class);
		CriteriaQuery<Long> countQuery = null;
		if(predicates.length > 0){
			Predicate or = cb.or(predicates);
			 baseQuery = q.select(c).where(or);
			 countQuery = qc.select(cb.count(qc.from(info.getJavaType()))).where(or);
		}else{
			baseQuery = q.select(c);
			countQuery = qc.select(cb.count(qc.from(info.getJavaType())));
		}
		
		TypedQuery<T> query = entityManager.createQuery(baseQuery);

		
		Long count = entityManager.createQuery(countQuery).getSingleResult();

		query.setFirstResult(details.getOffset());
		query.setMaxResults(details.getPageSize());
		List<T> list = query.getResultList();

		return new PageImpl<T>(list, details, count);
	}

	private List<String> getFields() throws InstantiationException, IllegalAccessException {
		Class<T> type = info.getJavaType();
		IEntity en = (IEntity) type.newInstance();
		return en.getDefaultSearchFields();

	}

}

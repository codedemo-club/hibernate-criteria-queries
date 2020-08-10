package club.codedemo.hibernatecriteriaqueries;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.List;


@Service
public class StudentService {

    @PersistenceContext
    EntityManager entityManager;

    /**
     * 获取session
     * <p>
     * 在这里获取了session也没有直接使用entityManager的原因是为了更贴近于原生的Hibernate
     *
     * @return
     */
    public Session getSession() {
        return (Session) this.entityManager.getDelegate();
    }

    /**
     * 查找所有
     *
     * @return
     */
    @Transactional
    public List<Student> findAll() {
        Session session = this.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        Root<Student> root = cq.from(Student.class);

        cq.select(root);

        return session.createQuery(cq).getResultList();
    }

    /**
     * 查找体重大于X的学生
     * <p>
     * 本方法中对findAll方法进行了抽象
     * 其中抽象出的query方法具有更高可复用性
     *
     * @param weight 体重
     * @return
     */
    @Transactional
    public List<Student> weightGt(Integer weight) {
        return this.query(((cb, cq, root) ->
                cq.select(root).where(cb.gt(root.get("weight"), weight))
        ));
    }

    /**
     * 小于表达式测试
     *
     * @param weight 体重
     * @return
     */
    @Transactional
    public List<Student> weightLt(Integer weight) {
        return this.query(((cb, cq, root) ->
                cq.select(root).where(cb.lt(root.get("weight"), weight))
        ));
    }

    /**
     * contains包含测试
     *
     * @param name
     * @return
     */
    @Transactional
    public List<Student> nameContains(String name) {
        return this.query(((cb, cq, root) ->
                cq.select(root).where(cb.like(root.get("name"), "%" + name + "%"))
        ));
    }

    /**
     * between测试
     *
     * @param minWeight 最小体重
     * @param maxWeight 最大体重
     * @return
     */
    @Transactional
    public List<Student> weightBetween(Integer minWeight, Integer maxWeight) {
        return this.query(((cb, cq, root) ->
                cq.select(root).where(cb.between(root.get("weight"), minWeight, maxWeight))
        ));
    }

    /**
     * isNull
     *
     * @return
     */
    @Transactional
    public List<Student> noIsNull() {
        return this.query(((cb, cq, root) ->
                cq.select(root).where(cb.isNull(root.get("no")))
        ));
    }

    /**
     * 不是null
     *
     * @return
     */
    @Transactional
    public List<Student> noIsNotNull() {
        return this.query(((cb, cq, root) ->
                cq.select(root).where(cb.isNotNull(root.get("no")))
        ));
    }

    /**
     * 传入查询条件的数组
     *
     * @param name
     * @return
     */
    @Transactional
    public List<Student> noIsNotNullAndNameLike(String name) {
        return this.query(((cb, cq, root) -> {
            Predicate[] predicates = new Predicate[2];
            predicates[0] = cb.isNotNull(root.get("no"));
            predicates[1] = cb.like(root.get("name"), name + "%");
            return cq.select(root).where(predicates);
        }));
    }

    /**
     * 两个条件做OR
     *
     * @param weight
     * @param name
     * @return
     */
    @Transactional
    public List<Student> weightGtOrNameLike(int weight, String name) {
        return this.query(((cb, cq, root) -> {
            Predicate weightGt = cb.gt(root.get("weight"), weight);
            Predicate nameLike = cb.like(root.get("name"), name + "%");
            return cq.select(root).where(cb.or(weightGt, nameLike));
        }));
    }

    /**
     * 两个查询条件做and
     *
     * @param weight
     * @param name
     * @return
     */
    @Transactional
    public List<Student> weightGtAndNameLike(int weight, String name) {
        return this.query(((cb, cq, root) -> {
            Predicate weightGt = cb.gt(root.get("weight"), weight);
            Predicate nameLike = cb.like(root.get("name"), name + "%");
            return cq.select(root).where(cb.and(weightGt, nameLike));
        }));
    }

    /**
     * 排序
     *
     * @return
     */
    @Transactional
    public List<Student> orderByWeightAndName() {
        return this.query(((cb, cq, root) -> cq.select(root).orderBy(
                cb.desc(root.get("weight")),
                cb.asc(root.get("name"))
        )));
    }

    /**
     * count
     *
     * @return
     */
    @Transactional
    public List<Long> count() {
        return this.query(((cb, cq, root) -> cq.select(cb.count(root))));
    }

    /**
     * average平均数
     *
     * @return
     */
    @Transactional
    public List<Double> average() {
        return this.query(((cb, cq, root) -> cq.select(cb.avg(root.get("weight")))));
    }

    /**
     * 更新操作
     *
     * @param no
     * @param name
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateNoByName(String no, String name) {
        Session session = this.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaUpdate<Student> criteriaUpdate = cb.createCriteriaUpdate(Student.class);
        Root<Student> root = criteriaUpdate.from(Student.class);

        criteriaUpdate.set("no", no);
        criteriaUpdate.where(cb.equal(root.get("name"), name));

        session.createQuery(criteriaUpdate).executeUpdate();
    }

    /**
     * 删除操作
     *
     * @param id
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long id) {
        Session session = this.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaDelete<Student> criteriaDelete = cb.createCriteriaDelete(Student.class);
        Root<Student> root = criteriaDelete.from(Student.class);

        criteriaDelete.where(cb.equal(root.get("id"), id));

        session.createQuery(criteriaDelete).executeUpdate();
    }

    /**
     * 抽象查询方法
     *
     * @param spec 查询接口
     * @return
     */
    private List query(Spec spec) {
        Session session = this.getSession();
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Student> cq = cb.createQuery(Student.class);
        Root<Student> root = cq.from(Student.class);

        cq = spec.toQuery(cb, cq, root);

        return session.createQuery(cq).getResultList();
    }


    /**
     * query中使用的标准查询接口
     */
    private interface Spec {
        /**
         * 转换为条件查询
         *
         * @param cb   创建者
         * @param cq   查询
         * @param root 根实体
         * @return
         */
        CriteriaQuery toQuery(CriteriaBuilder cb, CriteriaQuery cq, Root root);
    }
}

package com.loan_system.lls.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.loan_system.lls.entity.Loan;

// @Repository
public interface LoanRepository extends JpaRepository<Loan, Integer> {

        // @Query(value = "select u from Loan u where u.loan_id =:loan_id")
        // public List<Loan> listLoans(@Param("loan_id") int loan_id);

        // @Query("SELECT l FROM Loan l WHERE (:#{#paramData.getField(0)} IS NULL OR l
        // IN (:#{#paramData.getField(0)})) " +
        // "AND (:#{#paramData.filterParams.loan_type_id} IS NULL OR l.loan_type_id IN
        // (:#{#paramData.filterParams.loan_type})) " +
        // "AND (:#{#paramData.filterParams.status} IS NULL OR l.status IN
        // (:#{#paramData.filterParams.status})) " +
        // "AND (:#{#paramData.filterParams.created_at} IS NULL OR l.created_at IN
        // (:#{#paramData.filterParams.created_at})) " +
        // :#{#paramData.getRequested_date()} :#{#paramData.getOffset()}
        // "ORDER BY l.requested_date DESC") :#{#paramData.getLoan_type_id()}
        // :#{#paramData.getLimit()}

        // @Query(value = "SELECT :#{#paramData.getColumns()} FROM loan_request WHERE
        // created_at in(:#{#paramData.getCreated_at()}) and "
        // + "loan_type_id in( :#{#paramData.getLoan_type_id()} ) and status in(
        // :#{#paramData.getStatus()} ) ORDER BY requested_date asc LIMIT 5 OFFSET 0 ",
        // nativeQuery = true)
        // List<Loan> findByParams(@Param("paramData") ListRequestDto paramData);

        // @Query("SELECT l FROM Loan l WHERE l.customer_id=:customer_id " +
        // "AND (:#{#paramData.filterParams.loan_type} IS NULL OR l.loan_type IN
        // (:#{#paramData.filterParams.loan_type})) " +
        // "AND (:#{#paramData.filterParams.status} IS NULL OR l.status IN
        // (:#{#paramData.filterParams.status})) " +
        // "AND (:#{#paramData.filterParams.created_at} IS NULL OR l.created_at IN
        // (:#{#paramData.filterParams.created_at})) " +
        // "ORDER BY l.requested_date DESC") @Param("customer_id") @Param("paramData")

        // @Procedure("GET_TOTAL_CARS_BY_MODEL")
        // List<Loan> findByCustomerIdAndParams(int customer_id, LoanParamData
        // paramData);

        @Query(nativeQuery = true, value = "call get_list1(:a,:b,:c,:d,:e,:f,:g,:h )")
        public String get_list1(@Param("a") String columns_input, @Param("b") String created_at,
                        @Param("c") String loan_type_id, @Param("d") String status,
                        @Param("e") String sort_by_column, @Param("f") String sort_by_order,
                        @Param("g") int limit_val, @Param("h") int offset_val);

        @Query(nativeQuery = true, value = "call CxLoanReqsts(:a,:cx,:b,:c,:d,:e,:f,:g,:h )")
        public String userLoanReqsts(@Param("a") String columns_input, @Param("cx") int customer_id,
                        @Param("b") String created_at,
                        @Param("c") String loan_type_id, @Param("d") String status,
                        @Param("e") String sort_by_column, @Param("f") String sort_by_order,
                        @Param("g") int limit_val, @Param("h") int offset_val);

        // @Query(nativeQuery = true, value = "call
        // loan_list_byID(:a,:cx,:b,:c,:d,:e,:f,:g,:h )")
        // public String loan_list_byID(@Param("a") String columns_input,@Param("cx")int
        // customer_id,@Param("b")String created_at,
        // @Param("c") String loan_type_id,@Param("d") String status,
        // @Param("e") String sort_by_column,@Param("f") String sort_by_order,
        // @Param("g") int limit_val,@Param("h") int offset_val);

}

package com.sonata.portfoliomanagement.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sonata.portfoliomanagement.model.RevenueBudgetSummary;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
@Repository
public interface RevenueBudgetSummaryRepository extends JpaRepository<RevenueBudgetSummary,Integer> {

    List<RevenueBudgetSummary> findByVertical(String getList);
    List<RevenueBudgetSummary> findByFinancialYear(int financialYear);
    List<RevenueBudgetSummary> findByAccount(String account);
    List<RevenueBudgetSummary> findByDeliveryManager(String deliveryManager);
    List<RevenueBudgetSummary> findByVerticalAndClassification(String vertical, String classification);
    List<RevenueBudgetSummary> findAllByVertical(String vertical);
    List<RevenueBudgetSummary> findByProjectName(String project);


    List<RevenueBudgetSummary> findByAccountIn(List<String> accounts);

    List<RevenueBudgetSummary> findByDeliveryManagerIn(List<String> deliveryManagers);

    List<RevenueBudgetSummary> findByVerticalIn(List<String> verticals);

    List<RevenueBudgetSummary> findByClassificationIn(List<String> classifications);

    List<RevenueBudgetSummary> findByProjectManagerIn(List<String> projectManagers);

    List<RevenueBudgetSummary> findByProjectNameIn(List<String> projectNames);


    List<RevenueBudgetSummary> findBydeliveryManager(String deliveryManager);
    List<RevenueBudgetSummary> findByquarter(String quarter);

    List<RevenueBudgetSummary> findByprojectManager(String getList);


    List<RevenueBudgetSummary> findByProjectManager(String projectManager);


    List<RevenueBudgetSummary> findAllByClassification(String classification);

    List<RevenueBudgetSummary> findByclassification(String getList);


    List<RevenueBudgetSummary> findByVerticalInAndClassificationIn(List<String> verticals, List<String> classifications);

    List<RevenueBudgetSummary> findByFinancialYearInAndProjectNameInAndVerticalInAndClassificationInAndDeliveryManagerInAndAccountInAndProjectManagerInAndQuarterIn(List<Integer> financialYear, List<String> projectList, List<String> verticalList, List<String> classificationList, List<String> dmList, List<String> accountList, List<String> pmList, List<String> quarterList);

    List<RevenueBudgetSummary> findByProjectManagerInAndClassificationIn(List<String> pmNames, List<String> classifications);

    List<RevenueBudgetSummary> findByAccountInAndClassificationIn(List<String> accounts, List<String> classifications);

    List<RevenueBudgetSummary> findByDeliveryManagerInAndClassificationIn(List<String> deliveryManagers, List<String> classifications);

    List<RevenueBudgetSummary> findGapByFinancialYearInAndProjectNameIn(List<Integer> years, List<String> projectNames);

    List<RevenueBudgetSummary> findGapByFinancialYearInAndProjectNameInAndQuarter(List<Integer> integers, List<String> projectNames, String quarter);


    void deleteByVerticalAndClassificationAndDeliveryDirectorAndDeliveryManagerAndAccountAndProjectManagerAndProjectNameAndFinancialYearAndQuarter(String vertical, String classification, String deliveryDirector, String deliveryManager, String account, String projectManager, String projectName, int financialYear, String quarter);


    boolean existsByVerticalAndClassificationAndDeliveryDirectorAndDeliveryManagerAndAccountAndProjectManagerAndProjectNameAndFinancialYearAndQuarterAndBudget(String vertical, String classification, String deliveryDirector, String deliveryManager, String account, String projectManager, String projectName, int financialYear, String quarter, float budget);

    RevenueBudgetSummary findByVerticalAndClassificationAndDeliveryDirectorAndDeliveryManagerAndAccountAndProjectManagerAndProjectNameAndFinancialYearAndQuarterAndBudget(String vertical, String classification, String deliveryDirector, String deliveryManager, String account, String projectManager, String projectName, int financialYear, String quarter, float budget);



    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END FROM DataEntry d " +
            "WHERE d.vertical = :vertical AND d.classification = :classification " +
            "AND d.deliveryDirector = :deliveryDirector AND d.deliveryManager = :deliveryManager " +
            "AND d.account = :account AND d.projectManager = :projectManager " +
            "AND d.projectName = :projectName AND d.financialYear = :financialYear " +
            "AND d.quarter = :quarter AND d.budget BETWEEN :budget - :epsilon AND :budget + :epsilon " +
            "AND d.month = :month")
    boolean existsByVerticalAndClassificationAndDeliveryDirectorAndDeliveryManagerAndAccountAndProjectManagerAndProjectNameAndFinancialYearAndQuarterAndBudgetAndMonth(
            @Param("vertical") String vertical,
            @Param("classification") String classification,
            @Param("deliveryDirector") String deliveryDirector,
            @Param("deliveryManager") String deliveryManager,
            @Param("account") String account,
            @Param("projectManager") String projectManager,
            @Param("projectName") String projectName,
            @Param("financialYear") int financialYear,
            @Param("quarter") String quarter,
            @Param("budget") float budget,
            @Param("epsilon") float epsilon,
            @Param("month") String month);

}

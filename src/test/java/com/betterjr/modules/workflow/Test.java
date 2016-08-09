package com.betterjr.modules.workflow;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.cache.Cache;
import org.snaker.engine.cache.memory.MemoryCacheManager;
import org.snaker.engine.core.SnakerEngineImpl;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Process;
import org.snaker.engine.entity.Task;
import org.snaker.engine.entity.WorkItem;
import org.snaker.engine.model.ProcessModel;
import org.snaker.engine.spring.SpringSnakerEngine;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.betterjr.modules.workflow.data.FlowCommand;
import com.betterjr.modules.workflow.data.FlowInput;
import com.betterjr.modules.workflow.utils.SnakerProcessModelGenerator;
import com.google.common.collect.Maps;

public class Test {
    private static final String DEFAULT_SEPARATOR = ".";
    /**
     * 流程定义对象cache名称
     */
    private static final String CACHE_ENTITY = "snaker.process.entity";
    /**
     * 流程id、name的cache名称
     */
    private static final String CACHE_NAME = "snaker.process.name";

    public static void main(String[] args) throws Exception {
        // TODO Auto-generated method stub
        String[] configs = new String[] { "spring-context-platform-dubbo-provider.xml" };
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(configs);
        context.start();

        testMyModel(context);

        /* System.in.read(); */
        exit(context);
    }

    private static void testMyModel(ClassPathXmlApplicationContext context) {

        SnakerEngineImpl engine = context.getBean(SpringSnakerEngine.class);
        MemoryCacheManager cacheManager = (MemoryCacheManager) context.getBean("cacheManager");

        String processId = "76a2d1fa253a404fba0fe76c5feaed8b";

        // 启动流程
        Map<String, Object> formParas = Maps.newHashMap();
        formParas.put(FlowInput.MoneyPara, new BigDecimal("10001"));
        initProcessCache(engine, cacheManager, processId);
        Order order = engine.startInstanceById(processId, "hewei", formParas);

        // 经理审批
        String cuOperators[] = new String[] { "4003", "4004", "4007", "4008" };
        for (String cuOperator : cuOperators) {
            formParas = Maps.newHashMap();
            formParas.put(FlowInput.MoneyPara, new BigDecimal("10001"));
            formParas.put("command", FlowCommand.GoNext);
            QueryFilter filter = new QueryFilter().setOrderId(order.getId()).setOperator(cuOperator);
            List<WorkItem> workItemList = engine.query().getWorkItems(null, filter);
            if (workItemList != null && workItemList.size() > 0) {
                System.out.println("Current tasks:" + workItemList.size());

                for (int index = 0; index < workItemList.size(); index++) {
                    String taskId = workItemList.get(index).getTaskId();
                    List<Task> tasks = engine.executeTask(taskId, cuOperator, formParas);
                    System.out.println("Next tasks:" + tasks.size());
                }
            }
            else {
                System.out.println("no task to execute");
            }
        }

        exit(context);
    }

    private static void initProcessCache(SnakerEngineImpl engine, MemoryCacheManager cacheManager, String processId) {
        ProcessModel processModel = new SnakerProcessModelGenerator().buildProcessModel();
        Process process = engine.process().getProcessById(processId);
        process.setModel(processModel);
        Cache<String, Process> processCache = cacheManager.getCache(CACHE_ENTITY);
        Cache<String, String> nameCache = cacheManager.getCache(CACHE_NAME);
        String processName = process.getName() + DEFAULT_SEPARATOR + process.getVersion();
        processCache.put(processName, process);
        nameCache.put(process.getId(), processName);
    }

    private static void test(ClassPathXmlApplicationContext context) {
        ProcessModel processModel = new SnakerProcessModelGenerator().buildProcessModel();

        SnakerEngineImpl engine = context.getBean(SpringSnakerEngine.class);

        String processId = "ec41f906691d434f8aab1e701368d1e9";

        // 启动流程
        Map<String, Object> formParas = Maps.newHashMap();
        formParas.put("apply.operator", "hewei,weihe");
        Order order = engine.startInstanceById(processId, "hewei", formParas);
        // Process process = engine.process().getProcessById(processId);
        // process.setModel(processModel);

        // 申请
        formParas = Maps.newHashMap();
        formParas.put("approveDept.operator", "hewei-1");
        formParas.put("command", FlowCommand.GoNext);
        List<Task> tasks = engine.query().getActiveTasks(new QueryFilter().setOrderId(order.getId()));
        if (tasks != null && tasks.size() > 0) {
            System.out.println("Current tasks:" + tasks.size());
            String cuOperator = "hewei";

            for (int index = 0; index < tasks.size(); index++) {
                Task task = tasks.get(index);
                tasks = engine.executeTask(task.getId(), cuOperator, formParas);
                System.out.println("Next tasks:" + tasks.size());
            }
        }
        else {
            System.out.println("no task to execute");
        }

        // 经理审批
        formParas = Maps.newHashMap();
        formParas.put("day", 5);
        formParas.put("approveBoss.operator", "hewei-2");
        formParas.put("command", FlowCommand.Rollback);
        tasks = engine.query().getActiveTasks(new QueryFilter().setOrderId(order.getId()));
        if (tasks != null && tasks.size() > 0) {
            System.out.println("Current tasks:" + tasks.size());
            String cuOperator = "hewei-1";

            for (int index = 0; index < tasks.size(); index++) {
                Task task = tasks.get(index);
                tasks = engine.executeTask(task.getId(), cuOperator, formParas);
                // tasks=engine.executeAndJumpTask(task.getId(), cuOperator, formParas, null);
                System.out.println("Next tasks:" + tasks.size());
            }
        }
        else {
            System.out.println("no task to execute");
        }

        // exit(context);
        // 总经理审批
        formParas = Maps.newHashMap();
        formParas.put("command", FlowCommand.GoNext);
        tasks = engine.query().getActiveTasks(new QueryFilter().setOrderId(order.getId()));
        if (tasks != null && tasks.size() > 0) {
            System.out.println("Current tasks:" + tasks.size());
            String cuOperator = "hewei-2";

            for (int index = 0; index < tasks.size(); index++) {
                Task task = tasks.get(index);
                tasks = engine.executeTask(task.getId(), cuOperator, formParas);
                System.out.println("Next tasks:" + tasks.size());
            }
        }
        else {
            System.out.println("no task to execute");
        }

        System.out.println(order);
    }

    private static void exit(ClassPathXmlApplicationContext context) {
        context.close();
        System.exit(0);
    }

    private static void testMyModelWithJump(ClassPathXmlApplicationContext context) {

        SnakerEngineImpl engine = context.getBean(SpringSnakerEngine.class);
        MemoryCacheManager cacheManager = (MemoryCacheManager) context.getBean("cacheManager");

        String processId = "76a2d1fa253a404fba0fe76c5feaed8b";

        // 启动流程
        Map<String, Object> formParas = Maps.newHashMap();
        formParas.put(FlowInput.MoneyPara, new BigDecimal("10001"));
        initProcessCache(engine, cacheManager, processId);
        Order order = engine.startInstanceById(processId, "hewei", formParas);

        // 经理审批
        String cuOperators[] = new String[] { "4003", "4004", "4007", "4008" };
        for (String cuOperator : cuOperators) {
            formParas = Maps.newHashMap();
            formParas.put(FlowInput.MoneyPara, new BigDecimal("10001"));
            formParas.put("command", FlowCommand.GoNext);
            QueryFilter filter = new QueryFilter().setOrderId(order.getId()).setOperator(cuOperator);
            List<WorkItem> workItemList = engine.query().getWorkItems(null, filter);
            if (workItemList != null && workItemList.size() > 0) {
                System.out.println("Current tasks:" + workItemList.size());

                for (int index = 0; index < workItemList.size(); index++) {
                    String taskId = workItemList.get(index).getTaskId();
                    List<Task> tasks = engine.executeTask(taskId, cuOperator, formParas);
                    System.out.println("Next tasks:" + tasks.size());
                }
            }
            else {
                System.out.println("no task to execute");
            }
        }

        exit(context);
    }

}

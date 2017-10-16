package com.betterjr.modules.workflow.snaker.core;

import java.util.Map;

import org.snaker.engine.core.TaskService;
import org.snaker.engine.entity.Task;
import org.snaker.engine.helper.AssertHelper;
import org.snaker.engine.helper.JsonHelper;
import org.snaker.engine.helper.StringHelper;

public class BetterTaskService extends TaskService {

    /**
     * 向指定任务移除参与者
     */
    @Override
    public void removeTaskActor(String taskId, String... actors) {
        Task task = access().getTask(taskId);
        AssertHelper.notNull(task, "指定的任务[id=" + taskId + "]不存在");
        if (actors == null || actors.length == 0) return;
        if (task.isMajor()) {
            access().removeTaskActor(task.getId(), actors);
            Map<String, Object> taskData = task.getVariableMap();
            String actorStr = (String) taskData.get(Task.KEY_ACTOR);
            if (StringHelper.isNotEmpty(actorStr)) {
                String[] actorArray = actorStr.split(",");
                StringBuilder newActor = new StringBuilder(actorStr.length());
                boolean isMatch;
                for (String actor : actorArray) {
                    isMatch = false;
                    if (StringHelper.isEmpty(actor)) continue;
                    for (String removeActor : actors) {
                        if (actor.equals(removeActor)) {
                            isMatch = true;
                            break;
                        }
                    }
                    if (isMatch) continue;
                    newActor.append(actor).append(",");
                }
                if (newActor.length() > 0) {
                    newActor.deleteCharAt(newActor.length() - 1);
                }
                taskData.put(Task.KEY_ACTOR, newActor.toString());
                task.setVariable(JsonHelper.toJson(taskData));
                access().updateTask(task);
            }
        }
    }

}

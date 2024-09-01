package asura.storage;

import asura.data.exception.AsuraException;
import asura.data.tasks.Deadline;
import asura.data.tasks.Event;
import asura.data.tasks.Task;
import asura.data.tasks.Todo;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Storage {

    private String filePath;

    public Storage(String filePath) throws AsuraException {
        this.filePath = filePath;
    }

    public List<Task> load() throws AsuraException {
        List<Task> taskList = new ArrayList<>();
        File data = new File(filePath);
        data.getParentFile().mkdirs();
        try {
            data.createNewFile();
            Scanner scanner = new Scanner(data);
            while (scanner.hasNextLine()) {
                String[] task = scanner.nextLine().split("\\|");
                int status = Integer.parseInt(task[1]);
                switch (task[0]) {
                case "T":
                    Todo todo = new Todo(task[2]);
                    taskList.add(todo);
                    break;
                case "E":
                    Event event = new Event(task[2], task[3], task[4]);
                    taskList.add(event);
                    break;
                case "D":
                    Deadline deadline = new Deadline(task[2], LocalDateTime.parse(task[3]));
                    taskList.add(deadline);
                    break;
                }
                if (status == 1) {
                    taskList.get(taskList.size() - 1).markAsDone();
                }
            }
        } catch (Exception e) {
            throw new AsuraException(e.getMessage());
        }

        return taskList;
    }

    public void save(List<Task> taskList) throws AsuraException {
        StringBuilder sb = new StringBuilder();
        for (Task task : taskList) {
            if (task instanceof Event event) {
                sb.append("E|").append(event.getIsDone() ? 1 : 0).append("|").append(event.getDescription()).append("|").append(event.getStart()).append("|").append(event.getEnd()).append("\n");
            } else if (task instanceof Todo todo) {
                sb.append("T|").append(todo.getIsDone() ? 1 : 0).append("|").append(todo.getDescription()).append("\n");
            } else if (task instanceof Deadline deadline) {
                sb.append("D|").append(deadline.getIsDone() ? 1 : 0).append("|").append(deadline.getDescription()).append("|").append(deadline.getBy()).append("\n");
            }
        }
        try {
            Files.write(Paths.get(filePath), sb.toString().getBytes());
        } catch (Exception e) {
            throw new AsuraException(e.getMessage());
        }
    }
}

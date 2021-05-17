package bfst21;

import com.sun.management.OperatingSystemMXBean;
import javafx.scene.text.Text;

import java.lang.management.ManagementFactory;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Debug {
    private ScheduledExecutorService executor;
    private final MapCanvas canvas;
    private final Text cpuProcess;
    private final Text cpuSystem;
    private final Text ttd;
    private final Text memoryUse;

    public Debug(MapCanvas canvas, Text cpuProcess, Text cpuSystem, Text ttd, Text memoryUse) {
        this.canvas = canvas;
        this.cpuProcess = cpuProcess;
        this.cpuSystem = cpuSystem;
        this.ttd = ttd;
        this.memoryUse = memoryUse;
        executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(updateStats, 0, 3, TimeUnit.SECONDS);
    }

    OperatingSystemMXBean bean = (com.sun.management.OperatingSystemMXBean) ManagementFactory
            .getOperatingSystemMXBean();
    Runtime runtime = Runtime.getRuntime();
    long processMemory = runtime.totalMemory() - runtime.freeMemory();

    Runnable updateStats = new Runnable() {
        public void run() {
            cpuProcess.textProperty().setValue("CPU Process Load: " + bean.getProcessCpuLoad());
            cpuSystem.textProperty().setValue("CPU System Load: " + bean.getCpuLoad() + "( " + bean.getSystemLoadAverage() + " average)");
            memoryUse.textProperty().setValue("Memory Use (Experimental): " + processMemory);
            long total = 0;
            for (long temp : canvas.redrawAverage) {
                total += temp;
            }
            ttd.textProperty().set("Redraw time (Rolling Average): ~" + TimeUnit.NANOSECONDS.toMillis(total / canvas.redrawAverage.length) + "ms (" + total / canvas.redrawAverage.length + "ns)");
        }
    };

    public void shutdownExecutor() {
        executor.shutdown();
    }
}

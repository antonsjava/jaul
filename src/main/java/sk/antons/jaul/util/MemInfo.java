/*
 *
 */
package sk.antons.jaul.util;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryUsage;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author antons
 */
public class MemInfo {

    int titleLength = 28;
    int memNumLength = 15;
    String numberFormat = "###,###,###,###,###";


    public static MemInfo instance() { return new MemInfo(); }
    public int titleLength() { return titleLength; }
    public MemInfo titleLength(int value) { this.titleLength = value; return this; }
    public int memNumLength() { return memNumLength; }
    public MemInfo memNumLength(int value) { this.memNumLength = value; return this; }
    public String numberFormat() { return numberFormat; }
    public MemInfo numberFormat(String value) { this.numberFormat = value; return this; }

    private String formatNumber(long num, int length) {
        DecimalFormat df = new DecimalFormat(numberFormat);
        return format((num < 0)?"":df.format(num), length, false);
    }

    private static String format(String text, int length, boolean alignleft) {
        if(text == null) text = "";
        if(text.length() > length) return text.substring(0, length);
        while(text.length()<length) {
            if(alignleft) text = text + " ";
            else text = " " + text;
        }
        return text;
    }

    private static String memory(long used, long free, long reserv, long max, int length) {
        StringBuilder sb = new StringBuilder();
        sb.append(" use: ");
        String text = null;
        if(used >= 0) text = String.valueOf(used);
        sb.append(format(text, length, false));

        sb.append(" free: ");
        text = null;
        if(free >= 0) text = String.valueOf(free);
        sb.append(format(text, length, false));

        sb.append(" reserv: ");
        text = null;
        if(reserv >= 0) text = String.valueOf(reserv);
        sb.append(format(text, length, false));

        sb.append(" max: ");
        text = null;
        if(max >= 0) text = String.valueOf(max);
        sb.append(format(text, length, false));

        return sb.toString();
    }

    private void generateBasic(StringBuilder report) {
        long reserv = Runtime.getRuntime().totalMemory();
        long free = Runtime.getRuntime().freeMemory();
        long used = reserv - free;
        long max = Runtime.getRuntime().maxMemory();
        report.append(format("", titleLength, true));
        report.append(" ");
        report.append(format("used", memNumLength, false));
        report.append(format("free", memNumLength, false));
        report.append(format("reserved", memNumLength, false));
        report.append(format("max", memNumLength, false));
        report.append('\n');

        report.append(format("Runtime", titleLength, true));
        report.append(" ");
        report.append(formatNumber(used, memNumLength));
        report.append(formatNumber(free, memNumLength));
        report.append(formatNumber(reserv, memNumLength));
        report.append(formatNumber(max, memNumLength));
        report.append('\n');

        report.append('\n');
    }

    private void generateDetail(StringBuilder report, String title, MemoryUsage mu) {
        long init = mu.getInit();
        long used = mu.getUsed();
        long reserv = mu.getCommitted();
        long max = mu.getMax();
        long free = reserv - used;
        report.append(format(title, titleLength, true));
        report.append(" ");
        report.append(formatNumber(used, memNumLength));
        report.append(formatNumber(free, memNumLength));
        report.append(formatNumber(reserv, memNumLength));
        report.append(formatNumber(max, memNumLength));
        report.append(formatNumber(init, memNumLength));
        report.append(note(title));
        report.append('\n');

    }

    private static String note(String title) {
        if(title == null) return "";
        else if("Heap".equals(title)) return "  -Xmx1g";
        else if(title.contains("Metaspace")) return "  -XX:MetaspaceSize=5m -XX:MaxMetaspaceSize=5m";
        else if(title.contains("ompress")) return "  -XX:CompressedClassSpaceSize=50m";
        else if(title.contains("CodeHeap")) return "  -XX:ReservedCodeCacheSize=5m";
        else if(title.contains("CodeCache")) return "  -XX:ReservedCodeCacheSize=5m";
        else return "";
    }

    private void generateDetail(StringBuilder report) {
        MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();

        report.append(format("", titleLength, true));
        report.append(" ");
        report.append(format("used", memNumLength, false));
        report.append(format("free", memNumLength, false));
        report.append(format("reserved", memNumLength, false));
        report.append(format("max", memNumLength, false));
        report.append(format("init", memNumLength, false));
        report.append('\n');


        long used = 0;
        long init = 0;
        long reserved = 0;
        long max = 0;
        MemoryUsage mu = mbean.getHeapMemoryUsage();
        used = used + mu.getUsed();
        init = init + mu.getInit();
        reserved = reserved + mu.getCommitted();
        max = max + mu.getMax();
        generateDetail(report, "Heap", mu);

        mu = mbean.getNonHeapMemoryUsage();
        used = used + mu.getUsed();
        init = init + mu.getInit();
        reserved = reserved + mu.getCommitted();
        max = max + mu.getMax();
        generateDetail(report, "NonHeap", mu);

        MemoryUsage m= new MemoryUsage(init, used, reserved, max);
        generateDetail(report, "", m);
        report.append('\n');

        List<MemoryPoolMXBean> list = ManagementFactory.getMemoryPoolMXBeans();
        if(list != null) {
            Collections.sort(list, new MPComparator());
            for(MemoryPoolMXBean memoryPoolMXBean : list) {
                String name = memoryPoolMXBean.getName();
                mu = memoryPoolMXBean.getUsage();
                if(mu != null) generateDetail(report, name, mu);
                mu = memoryPoolMXBean.getPeakUsage();
                if(mu != null) generateDetail(report, name + " peak", mu);
                mu = memoryPoolMXBean.getCollectionUsage();
                if(mu != null) generateDetail(report, name + " col", mu);
        		report.append('\n');
            }
        }

        report.append('\n');


        List<BufferPoolMXBean> pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        if(pools != null) {
            for(BufferPoolMXBean pool : pools) {
                init = -1;
                used = pool.getMemoryUsed();
                long reserv = -1;
                max = pool.getTotalCapacity();
                long free = max - used;
                report.append(format(pool.getName() + " (" + pool.getCount()+")", titleLength, true));
                report.append(" ");
                report.append(formatNumber(used, memNumLength));
                report.append(formatNumber(free, memNumLength));
                report.append(formatNumber(reserv, memNumLength));
                report.append(formatNumber(max, memNumLength));
                report.append(formatNumber(init, memNumLength));
                report.append('\n');
            }
        }

        report.append('\n');

        ThreadMXBean tbean = ManagementFactory.getThreadMXBean();

        report.append(format("", titleLength, true));
        report.append(" ");
        report.append(format("current", memNumLength, false));
        report.append(format("peak", memNumLength, false));
        report.append(format("total", memNumLength, false));
        report.append('\n');
        report.append(format("Threads", titleLength, true));
        report.append(" ");
        report.append(formatNumber(tbean.getThreadCount(), memNumLength));
        report.append(formatNumber(tbean.getPeakThreadCount(), memNumLength));
        report.append(formatNumber(tbean.getTotalStartedThreadCount(), memNumLength));
        report.append('\n');
        report.append('\n');

    }

    private void generateCurrentSettings(StringBuilder report) {
        report.append(" current X options\n");
        final RuntimeMXBean memMXBean = ManagementFactory.getRuntimeMXBean();
        final List<String> jvmArgs = memMXBean.getInputArguments();
        for (final String jvmArg : jvmArgs) {
            if (jvmArg.startsWith("-X")) {
                report.append("   ").append(jvmArg).append("\n");
            }
        }
        report.append(" other possible options\n");
        report.append("   -XX:MaxRAMPercentage=75\n");
        report.append("   -XX:MaxDirectMemorySize=5m\n");
        report.append('\n');
    }

    public String generateReport() {
        StringBuilder report = new StringBuilder();
        generateBasic(report);
        generateDetail(report);
        generateCurrentSettings(report);
        return report.toString();
    }

    private static class MPComparator implements Comparator<MemoryPoolMXBean> {

        public int compare(MemoryPoolMXBean o1, MemoryPoolMXBean o2) {
            String s1 = null;
            String s2 = null;
            if(o1 != null) s1 = o1.getName();
            if(o2 != null) s2 = o2.getName();
            if(s1 == null) s1 = "";
            if(s2 == null) s2 = "";
            return s1.compareTo(s2);
        }

    }

    public static void main(String[] argv) {
        System.out.println(MemInfo.instance().generateReport());

    }

}

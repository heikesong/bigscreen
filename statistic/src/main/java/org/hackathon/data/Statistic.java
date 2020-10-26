package org.hackathon.data;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.GenericOptionsParser;

public class Statistic {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: Statistic <in> <out>");
            System.exit(2);
        }
        String input = otherArgs[0];
        String output = otherArgs[1];
        DataWashing.generate(new String[]{input, output+"/data"});
        // project count
        ProjectCount.generate(new String[]{input, output+"/project"});
        // unique people count
        GenDataCount.generate(new String[]{output+"/data", output+"/mail", "2"});
        // company count
        GenDataCount.generate(new String[]{output+"/data", output+"/company", "3"});
        // province count
        GenDataCount.generate(new String[]{output+"/data", output+"/province", "4"});
        // city count
        GenDataCount.generate(new String[]{output+"/data", output+"/city", "5"});
    }
}
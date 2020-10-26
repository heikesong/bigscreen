package org.hackathon.data;
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.util.StringTokenizer;

import me.ihxq.projects.pna.Attribution;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import me.ihxq.projects.pna.PhoneNumberInfo;
import me.ihxq.projects.pna.PhoneNumberLookup;

public class DataWashing {

    public static class TokenizerMapper
            extends Mapper<Object, Text, Text, NullWritable> {

        private Text cleanline = new Text();

        private String getCompany(String company) {
            if(company.toLowerCase().startsWith("huawei")
                    || company.startsWith("华为") ) {
                return "华为";
            }
            if(company.toLowerCase().startsWith("inspur")
                    || company.startsWith("浪潮") ) {
                return "浪潮";
            }
            if(company.toLowerCase().startsWith("intel")
                    || company.startsWith("英特尔") ) {
                return  "Intel";
            }
            if(company.toLowerCase().startsWith("zte")
                    || company.startsWith("中兴") ) {
                return "中兴";
            }
            if(company.toLowerCase().startsWith("鸿合科技")) {
                return "鸿合";
            }
            if(company.toLowerCase().startsWith("hunan normal university")) {
                return "HNU";
            }
            if(company.toLowerCase().startsWith("fiberhome")) {
                return "烽火";
            }
            if(company.startsWith("中国移动")) {
                return "移动";
            }
            if(company.startsWith("蚂蚁集团")) {
                return "蚂蚁";
            }
            if(company.startsWith("麒麟")) {
                return "麒麟";
            }
            if(company.equals("")) {
                return "未知";
            }
            return company;
        }

        public void map(Object key, Text value, Context context
        ) throws IOException, InterruptedException {
            String line = value.toString();
            if (line.startsWith("#")) {
                String[] records = line.split(",|，");
                if (records.length == 5) {
                    // TODO: clean the header and example data
                    String name = records[1].trim();
                    String number = records[2].trim();
                    String mail = records[3].trim();
                    String company = getCompany(records[4].trim());
                    cleanline.set(name + "," + number + "," + mail + "," + company);
                    context.write(new Text(cleanline), NullWritable.get());
                } else {
                    System.out.println("Failed to split: " + line);
                }
            }
        }
    }

    public static class IntSumReducer
            extends Reducer<Text, NullWritable, Text, NullWritable> {

        public void reduce(Text key, Iterable<NullWritable> values,
                           Context context
        ) throws IOException, InterruptedException {
            // generate city info from number
            PhoneNumberLookup look = new PhoneNumberLookup();
            String city = look.lookup(key.toString().split(",")[1])
                    .map(PhoneNumberInfo::getAttribution)
                    .map(Attribution::getCity)
                    .orElse("未知");
            String province = look.lookup(key.toString().split(",")[1])
                    .map(PhoneNumberInfo::getAttribution)
                    .map(Attribution::getProvince)
                    .orElse("未知");
            context.write(new Text(key.toString() + "," + province + "," + city), NullWritable.get());
        }
    }

    public static int generate(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherArgs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if (otherArgs.length != 2) {
            System.err.println("Usage: DataWashing <in> <out>");
            System.exit(2);
        }

        Job job = new Job(conf, "data washing");
        job.setJarByClass(DataWashing.class);
        job.setMapperClass(TokenizerMapper.class);
        job.setReducerClass(IntSumReducer.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);
        FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));

        return (job.waitForCompletion(true) ? 0 : 1);
    }
}
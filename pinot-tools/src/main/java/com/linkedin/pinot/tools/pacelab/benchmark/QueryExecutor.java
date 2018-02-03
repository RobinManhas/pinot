/**
 * Copyright (C) 2014-2016 LinkedIn Corp. (pinot-core@linkedin.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkedin.pinot.tools.pacelab.benchmark;

import com.linkedin.pinot.tools.admin.command.PostQueryCommand;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public abstract class QueryExecutor {
    protected Properties config;
    protected PostQueryCommand postQueryCommand;
    protected int testDuration;
    protected String _dataDir;

    public static QueryExecutor getInstance(){
        return null;
    }


    public static List<QueryExecutor> getTableExecutors() {
        List<QueryExecutor> queryExecutors = new ArrayList<>();
        //queryExecutors.add(JobApplyQueryExecutor.getInstance());
        queryExecutors.add(ProfileViewQueryExecutor.getInstance());
        return queryExecutors;
    }

    public void start() throws InterruptedException {
        loadConfig();
        int threadCnt = Integer.parseInt(config.getProperty("ThreadCount"));
        ExecutorService threadPool = Executors.newFixedThreadPool(threadCnt);
        QueryTask queryTask = getTask(config);
        queryTask.setPostQueryCommand(this.postQueryCommand);
        threadPool.execute(queryTask);
        threadPool.awaitTermination(this.testDuration, TimeUnit.SECONDS);
        threadPool.shutdownNow();
    }

    public void loadConfig() {
        String configFile = getConfigFile();
        config = new Properties();
        try {
            InputStream in = QueryExecutor.class.getClassLoader().getResourceAsStream(configFile);
            config.load(in);
        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("IOException");
            e.printStackTrace();
        }
    }

    public void setPostQueryCommand(PostQueryCommand postQueryCommand) {
        this.postQueryCommand = postQueryCommand;
    }

    public abstract String getConfigFile();

    public abstract QueryTask getTask(Properties config);

    public void setTestDuration(int testDuration) {
        this.testDuration = testDuration;
    }
    public void setDataDir(String dataDir)
    {
        _dataDir = dataDir;
    }
    public String getDataDir()
    {
        return _dataDir;
    }
}

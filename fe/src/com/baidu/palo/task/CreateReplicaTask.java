// Copyright (c) 2017, Baidu.com, Inc. All Rights Reserved

// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package com.baidu.palo.task;

import com.baidu.palo.catalog.Column;
import com.baidu.palo.catalog.KeysType;
import com.baidu.palo.common.MarkedCountDownLatch;
import com.baidu.palo.thrift.TColumn;
import com.baidu.palo.thrift.TCreateTabletReq;
import com.baidu.palo.thrift.TStorageMedium;
import com.baidu.palo.thrift.TStorageType;
import com.baidu.palo.thrift.TTabletSchema;
import com.baidu.palo.thrift.TTaskType;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CreateReplicaTask extends AgentTask {
    private static final Logger LOG = LogManager.getLogger(CreateReplicaTask.class);

    private short shortKeyColumnCount;
    private int schemaHash;

    private long version;
    private long versionHash;

    private KeysType keysType;
    private TStorageType storageType;
    private TStorageMedium storageMedium;

    private List<Column> columns;

    // bloom filter columns
    private Set<String> bfColumns;
    private double bfFpp;

    // used for synchronous process
    private MarkedCountDownLatch latch;

    public CreateReplicaTask(long backendId, long dbId, long tableId, long partitionId, long indexId, long tabletId,
                             short shortKeyColumnCount, int schemaHash, long version, long versionHash,
                             KeysType keysType, TStorageType storageType,
                             TStorageMedium storageMedium, List<Column> columns,
                             Set<String> bfColumns, double bfFpp, MarkedCountDownLatch latch) {
        super(null, backendId, TTaskType.CREATE, dbId, tableId, partitionId, indexId, tabletId);

        this.shortKeyColumnCount = shortKeyColumnCount;
        this.schemaHash = schemaHash;

        this.version = version;
        this.versionHash = versionHash;

        this.keysType = keysType;
        this.storageType = storageType;
        this.storageMedium = storageMedium;

        this.columns = columns;

        this.bfColumns = bfColumns;
        this.bfFpp = bfFpp;

        this.latch = latch;
    }
    
    public void countDownLatch(long backendId, long tabletId) {
        if (this.latch != null) {
            if (latch.markedCountDown(backendId, tabletId)) {
                LOG.debug("CreateReplicaTask current latch count: {}, backend: {}, tablet:{}",
                          latch.getCount(), backendId, tabletId);
            }
        }
    }

    public TCreateTabletReq toThrift() {
        TCreateTabletReq createTabletReq = new TCreateTabletReq();
        createTabletReq.setTablet_id(tabletId);

        TTabletSchema tSchema = new TTabletSchema();
        tSchema.setShort_key_column_count(shortKeyColumnCount);
        tSchema.setSchema_hash(schemaHash);
        tSchema.setKeys_type(keysType.toThrift());
        tSchema.setStorage_type(storageType);

        List<TColumn> tColumns = new ArrayList<TColumn>();
        for (Column column : columns) {
            TColumn tColumn = column.toThrift();
            // is bloom filter column
            if (bfColumns != null && bfColumns.contains(column.getName())) {
                tColumn.setIs_bloom_filter_column(true);
            }
            tColumns.add(tColumn);
        }
        tSchema.setColumns(tColumns);

        if (bfColumns != null) {
            tSchema.setBloom_filter_fpp(bfFpp);
        }
        createTabletReq.setTablet_schema(tSchema);

        createTabletReq.setVersion(version);
        createTabletReq.setVersion_hash(versionHash);

        createTabletReq.setStorage_medium(storageMedium);


        return createTabletReq;
    }
}

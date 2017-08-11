// Modifications copyright (C) 2017, Baidu.com, Inc.
// Copyright 2017 The Apache Software Foundation

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

package com.baidu.palo.common.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.baidu.palo.thrift.TCounter;
import com.baidu.palo.thrift.TCounterType;
import com.baidu.palo.thrift.TRuntimeProfileNode;
import com.baidu.palo.thrift.TRuntimeProfileTree;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RuntimeProfileTest {
    
    @Test
    public void testSortChildren() {
        RuntimeProfile profile = new RuntimeProfile();
        // init profile
        RuntimeProfile profile1 = new RuntimeProfile();
        RuntimeProfile profile2 = new RuntimeProfile();
        RuntimeProfile profile3 = new RuntimeProfile();
        profile1.getCounterTotalTime().setValue(1);
        profile2.getCounterTotalTime().setValue(3);
        profile3.getCounterTotalTime().setValue(2);
        profile.addChild(profile1); 
        profile.addChild(profile2); 
        profile.addChild(profile3);
        // compare
        profile.sortChildren();
        // check result
        long time0 = profile.getChildList().get(0).first.getCounterTotalTime().getValue();
        long time1 = profile.getChildList().get(1).first.getCounterTotalTime().getValue();
        long time2 = profile.getChildList().get(2).first.getCounterTotalTime().getValue();
        
        Assert.assertEquals(3, time0);
        Assert.assertEquals(2, time1);
        Assert.assertEquals(1, time2);
    }
    
    @Test
    public void testInfoStrings() {
        RuntimeProfile profile = new RuntimeProfile("profileName");

        // not exists key
        Assert.assertNull(profile.getInfoString("key"));
        // normal add and get
        profile.addInfoString("key", "value");
        String value = profile.getInfoString("key");
        Assert.assertNotNull(value);
        Assert.assertEquals(value, "value");
        // from thrift to profile and first update
        TRuntimeProfileTree tprofileTree = new TRuntimeProfileTree();
        TRuntimeProfileNode tnode = new TRuntimeProfileNode();
        tprofileTree.addToNodes(tnode);
        tnode.info_strings = new HashMap<String, String>();
        tnode.info_strings.put("key", "value2");
        tnode.info_strings.put("key3", "value3");
        tnode.info_strings_display_order = new ArrayList<String>();
        tnode.info_strings_display_order.add("key");
        tnode.info_strings_display_order.add("key3");
      
        profile.update(tprofileTree);
        Assert.assertEquals(profile.getInfoString("key"), "value2");
        Assert.assertEquals(profile.getInfoString("key3"), "value3");
        // second update
        tnode.info_strings.put("key", "value4");
        
        profile.update(tprofileTree);
        Assert.assertEquals(profile.getInfoString("key"), "value4");
        
        StringBuilder builder = new StringBuilder();
        profile.prettyPrint(builder, "");
        Assert.assertEquals(builder.toString(), 
                "profileName:\n  key: value4\n  key3: value3\n");
    }
    
    @Test
    public void testCounter() {
        RuntimeProfile profile = new RuntimeProfile();
        profile.addCounter("key", TCounterType.UNIT, "");
        Assert.assertNotNull(profile.getCounterMap().get("key"));
        Assert.assertNull(profile.getCounterMap().get("key2"));
        profile.getCounterMap().get("key").setValue(1);
        Assert.assertEquals(profile.getCounterMap().get("key").getValue(), 1);
    }
    
    @Test
    public void testUpdate() throws IOException {
        RuntimeProfile profile = new RuntimeProfile("REAL_ROOT");
        /*  the profile tree
         *                      ROOT(time=5s info[key=value])
         *                  A(time=2s)            B(time=1s info[BInfo1=BValu1;BInfo2=BValue2])
         *       A_SON(time=10ms counter[counterA1=1; counterA2=2; counterA1Son=3])      
         */
        TRuntimeProfileTree tprofileTree = new TRuntimeProfileTree();
        TRuntimeProfileNode tnodeRoot = new TRuntimeProfileNode();
        TRuntimeProfileNode tnodeA = new TRuntimeProfileNode();
        TRuntimeProfileNode tnodeB = new TRuntimeProfileNode();
        TRuntimeProfileNode tnodeASon = new TRuntimeProfileNode();
        tnodeRoot.num_children = 2;
        tnodeA.num_children = 1;
        tnodeASon.num_children = 0;
        tnodeB.num_children = 0;
        tprofileTree.addToNodes(tnodeRoot);
        tprofileTree.addToNodes(tnodeA);
        tprofileTree.addToNodes(tnodeASon);
        tprofileTree.addToNodes(tnodeB);
        tnodeRoot.info_strings = new HashMap<String, String>();
        tnodeRoot.info_strings.put("key", "value");
        tnodeRoot.info_strings_display_order = new ArrayList<String>();
        tnodeRoot.info_strings_display_order.add("key");
        tnodeRoot.counters = Lists.newArrayList();
        tnodeA.counters = Lists.newArrayList();
        tnodeB.counters = Lists.newArrayList();
        tnodeASon.counters = Lists.newArrayList();
       
        tnodeRoot.counters.add(new TCounter("TotalTime", TCounterType.TIME_NS, 3000000000L));
        tnodeA.counters.add(new TCounter("TotalTime", TCounterType.TIME_NS, 1000000000L));
        tnodeB.counters.add(new TCounter("TotalTime", TCounterType.TIME_NS, 1000000000L));
        tnodeASon.counters.add(new TCounter("TotalTime", TCounterType.TIME_NS, 10000000));
        tnodeASon.counters.add(new TCounter("counterA1", TCounterType.UNIT, 1));
        tnodeASon.counters.add(new TCounter("counterA2", TCounterType.BYTES, 1234567L));
        tnodeASon.counters.add(new TCounter("counterA1Son", TCounterType.UNIT, 3));
        tnodeASon.child_counters_map = Maps.newHashMap();
        
        Set<String> set1 = Sets.newHashSet();
        set1.add("counterA1");
        set1.add("counterA2");
        tnodeASon.child_counters_map.put("", set1);
        Set<String> set2 = Sets.newHashSet();
        set2.add("counterA1Son");
        tnodeASon.child_counters_map.put("counterA1", set2);
        tnodeB.info_strings = Maps.newHashMap();
        tnodeB.info_strings_display_order = Lists.newArrayList();
        tnodeB.info_strings.put("BInfo1", "BValue1");
        tnodeB.info_strings.put("BInfo2", "BValue2");
        tnodeB.info_strings_display_order.add("BInfo2");
        tnodeB.info_strings_display_order.add("BInfo1");
        tnodeRoot.indent = true;
        tnodeA.indent = true;
        tnodeB.indent = true;
        tnodeASon.indent = true;
        tnodeRoot.name = "ROOT";
        tnodeA.name = "A";
        tnodeB.name = "B";
        tnodeASon.name = "ASON";
        
        profile.update(tprofileTree);
        StringBuilder builder = new StringBuilder();
        profile.computeTimeInProfile();
        profile.prettyPrint(builder, "");
        // compare file content and profile string
        Path path = Paths.get(getClass().getClassLoader().getResource("data/qe/profile.dat").getPath());
        String fileContent = new String(Files.readAllBytes(path));
        Assert.assertEquals(fileContent.replace("\n", "").replace("\r", ""), 
                builder.toString().replace("\n", "").replace("\r", ""));
    } 
}

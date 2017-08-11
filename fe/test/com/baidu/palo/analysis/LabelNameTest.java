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

package com.baidu.palo.analysis;

import com.baidu.palo.common.AnalysisException;

import org.junit.Assert;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

public class LabelNameTest {
    private Analyzer analyzer;

    @Before
    public void setUp() {
        analyzer = EasyMock.createMock(Analyzer.class);
        EasyMock.expect(analyzer.getClusterName()).andReturn("testCluster").anyTimes();
    }

    @Test
    public void testNormal() throws AnalysisException {
        EasyMock.expect(analyzer.getDefaultDb()).andReturn("testDb").anyTimes();
        EasyMock.replay(analyzer);

        LabelName label = new LabelName("testDb", "testLabel");
        label.analyze(analyzer);
        Assert.assertEquals("`testCluster:testDb`.`testLabel`", label.toString());

        label = new LabelName("", "testLabel");
        label.analyze(analyzer);
        Assert.assertEquals("`testCluster:testDb`.`testLabel`", label.toString());
        Assert.assertEquals("testCluster:testDb", label.getDbName());
        Assert.assertEquals("testLabel", label.getLabelName());
    }

    @Test(expected = AnalysisException.class)
    public void testNoDb() throws AnalysisException {
        EasyMock.expect(analyzer.getDefaultDb()).andReturn(null).anyTimes();
        EasyMock.replay(analyzer);

        LabelName label = new LabelName("", "testLabel");
        label.analyze(analyzer);
        Assert.fail("No exception throws");
    }

    @Test(expected = AnalysisException.class)
    public void testNoLabel() throws AnalysisException {
        EasyMock.expect(analyzer.getDefaultDb()).andReturn("testDb").anyTimes();
        EasyMock.replay(analyzer);

        LabelName label = new LabelName("", "");
        label.analyze(analyzer);
        Assert.fail("No exception throws");
    }

}
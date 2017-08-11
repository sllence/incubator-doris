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

package com.baidu.palo.planner;

import com.baidu.palo.analysis.Analyzer;
import com.baidu.palo.analysis.Expr;
import com.baidu.palo.analysis.SortInfo;
import com.baidu.palo.analysis.TupleId;
import com.baidu.palo.common.InternalException;
import com.baidu.palo.thrift.TExchangeNode;
import com.baidu.palo.thrift.TPlanNode;
import com.baidu.palo.thrift.TPlanNodeType;
import com.baidu.palo.thrift.TSortInfo;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Receiver side of a 1:n data stream. Logically, an ExchangeNode consumes the data
 * produced by its children. For each of the sending child nodes the actual data
 * transmission is performed by the DataStreamSink of the PlanFragment housing
 * that child node. Typically, an ExchangeNode only has a single sender child but,
 * e.g., for distributed union queries an ExchangeNode may have one sender child per
 * union operand.
 *
 * If a (optional) SortInfo field is set, the ExchangeNode will merge its
 * inputs on the parameters specified in the SortInfo object. It is assumed that the
 * inputs are also sorted individually on the same SortInfo parameter.
 */
public class ExchangeNode extends PlanNode {
    private static final Logger LOG = LogManager.getLogger(ExchangeNode.class);

    // The parameters based on which sorted input streams are merged by this
    // exchange node. Null if this exchange does not merge sorted streams
    private SortInfo mergeInfo;

    // Offset after which the exchange begins returning rows. Currently valid
    // only if mergeInfo_ is non-null, i.e. this is a merging exchange node.
    private long offset;

    /**
     * Create ExchangeNode that consumes output of inputNode.
     * An ExchangeNode doesn't have an input node as a child, which is why we
     * need to compute the cardinality here.
     */
    public ExchangeNode(PlanNodeId id, PlanNode inputNode, boolean copyConjuncts) {
        super(id, inputNode, "EXCHANGE");
        offset = 0;
        children.add(inputNode);
        if (!copyConjuncts) {
            this.conjuncts = Lists.newArrayList();
        }
        if (hasLimit()) {
            cardinality = Math.min(limit, inputNode.cardinality);
        } else {
            cardinality = inputNode.cardinality;
        }
        // Only apply the limit at the receiver if there are multiple senders.
        if (inputNode.getFragment().isPartitioned()) limit = inputNode.limit;
        computeTupleIds();
    }

    @Override
    public void computeTupleIds() {
        clearTupleIds();
        tupleIds.addAll(getChild(0).getTupleIds());
        tblRefIds.addAll(getChild(0).getTblRefIds());
        nullableTupleIds.addAll(getChild(0).getNullableTupleIds());
    }

    @Override
    public void init(Analyzer analyzer) throws InternalException {
        super.init(analyzer);
        Preconditions.checkState(conjuncts.isEmpty());
    }

    /**
     * Set the parameters used to merge sorted input streams. This can be called
     * after init().
     */
    public void setMergeInfo(SortInfo info, long offset) {
        this.mergeInfo = info;
        this.offset = offset;
        this.planNodeName = "MERGING-EXCHANGE";
    }

    @Override
    protected void toThrift(TPlanNode msg) {
        msg.node_type = TPlanNodeType.EXCHANGE_NODE;
        msg.exchange_node = new TExchangeNode();
        for (TupleId tid : tupleIds) {
            msg.exchange_node.addToInput_row_tuples(tid.asInt());
        }
        if (mergeInfo != null) {
            TSortInfo sortInfo = new TSortInfo(
                Expr.treesToThrift(mergeInfo.getOrderingExprs()), mergeInfo.getIsAscOrder(),
                mergeInfo.getNullsFirst());
            msg.exchange_node.setSort_info(sortInfo);
            msg.exchange_node.setOffset(offset);
        }
    }

    @Override
    protected String debugString() {
        ToStringHelper helper = Objects.toStringHelper(this);
        helper.addValue(super.debugString());
        helper.add("offset", offset);
        return helper.toString();
    }

    @Override
    public int getNumInstances() {
        return numInstances;
    }

}

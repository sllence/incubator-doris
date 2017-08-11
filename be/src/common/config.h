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

#ifndef BDG_PALO_BE_SRC_COMMON_CONFIG_H
#define BDG_PALO_BE_SRC_COMMON_CONFIG_H

#include "configbase.h"

namespace palo {
namespace config {
    // cluster id
    CONF_Int32(cluster_id, "-1");
    // port on which ImpalaInternalService is exported
    CONF_Int32(be_port, "9060");
    CONF_Int32(be_rpc_port, "10060");

    ////
    //// tcmalloc gc parameter
    ////
    // min memory for TCmalloc, when used memory is smaller than this, do not returned to OS
    CONF_Int64(tc_use_memory_min, "10737418240");
    // free memory rate.[0-100]
    CONF_Int64(tc_free_memory_rate, "20");

    // process memory limit specified as number of bytes
    // ('<int>[bB]?'), megabytes ('<float>[mM]'), gigabytes ('<float>[gG]'),
    // or percentage of the physical memory ('<int>%').
    // defaults to bytes if no unit is given"
    CONF_String(mem_limit, "80%");

    // the port heartbeat service used
    CONF_Int32(heartbeat_service_port, "9050");
    // the count of heart beat service
    CONF_Int32(heartbeat_service_thread_count, "1");
    // the count of thread to create table
    CONF_Int32(create_table_worker_count, "3");
    // the count of thread to drop table
    CONF_Int32(drop_table_worker_count, "3");
    // the count of thread to batch load
    CONF_Int32(push_worker_count_normal_priority, "3");
    // the count of thread to high priority batch load
    CONF_Int32(push_worker_count_high_priority, "3");
    // the count of thread to delete
    CONF_Int32(delete_worker_count, "3");
    // the count of thread to alter table
    CONF_Int32(alter_table_worker_count, "3");
    // the count of thread to clone
    CONF_Int32(clone_worker_count, "3");
    // the count of thread to clone
    CONF_Int32(storage_medium_migrate_count, "1");
    // the count of thread to cancel delete data
    CONF_Int32(cancel_delete_data_worker_count, "3");
    // the count of thread to check consistency
    CONF_Int32(check_consistency_worker_count, "1");
    // the count of thread to upload
    CONF_Int32(upload_worker_count, "3");
    // the count of thread to restore
    CONF_Int32(restore_worker_count, "3");
    // the count of thread to make snapshot
    CONF_Int32(make_snapshot_worker_count, "5");
    // the count of thread to release snapshot
    CONF_Int32(release_snapshot_worker_count, "5");
    // the interval time(seconds) for agent report tasks signatrue to dm
    CONF_Int32(report_task_interval_seconds, "10");
    // the interval time(seconds) for agent report disk state to dm
    CONF_Int32(report_disk_state_interval_seconds, "600");
    // the interval time(seconds) for agent report olap table to dm
    CONF_Int32(report_olap_table_interval_seconds, "600");
    // the timeout(seconds) for alter table
    CONF_Int32(alter_table_timeout_seconds, "86400");
    // the timeout(seconds) for make snapshot
    CONF_Int32(make_snapshot_timeout_seconds, "600");
    // the timeout(seconds) for release snapshot
    CONF_Int32(release_snapshot_timeout_seconds, "600");
    // the max download speed(KB/s)
    CONF_Int32(max_download_speed_kbps, "50000");
    // download low speed limit(KB/s)
    CONF_Int32(download_low_speed_limit_kbps, "50");
    // download low speed time(seconds)
    CONF_Int32(download_low_speed_time, "300");
    // curl verbose mode
    CONF_Int64(curl_verbose_mode, "1");
    // seconds to sleep for each time check table status
    CONF_Int32(check_status_sleep_time_seconds, "10");
    // sleep time for one second
    CONF_Int32(sleep_one_second, "1");
    // sleep time for five seconds
    CONF_Int32(sleep_five_seconds, "5");
    // trans file tools dir
    CONF_String(trans_file_tool_path, "${PALO_HOME}/tools/trans_file_tool/trans_files.sh");
    // agent tmp dir
    CONF_String(agent_tmp_dir, "${PALO_HOME}/tmp");

    // log dir
    CONF_String(sys_log_dir, "${PALO_HOME}/log");
    // INFO, WARNING, ERROR, FATAL
    CONF_String(sys_log_level, "INFO");
    // TIME-DAY, TIME-HOUR, SIZE-MB-nnn
    CONF_String(sys_log_roll_mode, "SIZE-MB-1024");
    // log roll num
    CONF_Int32(sys_log_roll_num, "10");
    // verbose log
    CONF_Strings(sys_log_verbose_modules, "");
    // log buffer level
    CONF_String(log_buffer_level, "");

    // Pull load task dir
    CONF_String(pull_load_task_dir, "${PALO_HOME}/var/pull_load");

    // the maximum number of bytes to display on the debug webserver's log page
    CONF_Int64(web_log_bytes, "1048576");
    // number of threads available to serve backend execution requests
    CONF_Int32(be_service_threads, "64");
    // key=value pair of default query options for Palo, separated by ','
    CONF_String(default_query_options, "");

    // If non-zero, Palo will output memory usage every log_mem_usage_interval'th fragment completion.
    CONF_Int32(log_mem_usage_interval, "0");
    // if non-empty, enable heap profiling and output to specified directory.
    CONF_String(heap_profile_dir, "");

    // cgroups allocated for palo
    CONF_String(palo_cgroups, "");

    // Controls the number of threads to run work per core.  It's common to pick 2x
    // or 3x the number of cores.  This keeps the cores busy without causing excessive
    // thrashing.
    CONF_Int32(num_threads_per_core, "3");
    // if true, compresses tuple data in Serialize
    CONF_Bool(compress_rowbatches, "true");
    // serialize and deserialize each returned row batch
    CONF_Bool(serialize_batch, "false");
    // interval between profile reports; in seconds
    CONF_Int32(status_report_interval, "5");
    // Local directory to copy UDF libraries from HDFS into
    CONF_String(local_library_dir, "${UDF_RUNTIME_DIR}");
    // number of olap scanner thread pool size
    CONF_Int32(palo_scanner_thread_pool_thread_num, "48");
    // number of olap scanner thread pool size
    CONF_Int32(palo_scanner_thread_pool_queue_size, "102400");
    // number of etl thread pool size
    CONF_Int32(etl_thread_pool_size, "8");
    // number of etl thread pool size
    CONF_Int32(etl_thread_pool_queue_size, "256");
    // port on which to run Palo test backend
    CONF_Int32(port, "20001");
    // default thrift client connect timeout(in seconds)
    CONF_Int32(thrift_connect_timeout_seconds, "3");
    // max row count number for single scan range
    CONF_Int32(palo_scan_range_row_count, "524288");
    // size of scanner queue between scanner thread and compute thread
    CONF_Int32(palo_scanner_queue_size, "1024");
    // single read execute fragment row size
    CONF_Int32(palo_scanner_row_num, "16384");
    // number of max scan keys
    CONF_Int32(palo_max_scan_key_num, "1024");
    // return_row / total_row
    CONF_Int32(palo_max_pushdown_conjuncts_return_rate, "90");
    // (Advanced) Maximum size of per-query receive-side buffer
    CONF_Int32(exchg_node_buffer_size_bytes, "10485760");
    // insert sort threadhold for sorter
    CONF_Int32(insertion_threadhold, "16");
    // the block_size every block allocate for sorter
    CONF_Int32(sorter_block_size, "8388608");
    // push_write_mbytes_per_sec
    CONF_Int32(push_write_mbytes_per_sec, "10");
    CONF_Int32(base_expansion_write_mbytes_per_sec, "5");

    CONF_Int64(column_dictionary_key_ration_threshold, "0");
    CONF_Int64(column_dictionary_key_size_threshold, "0");
    // if true, output IR after optimization passes
    CONF_Bool(dump_ir, "false");
    // if set, saves the generated IR to the output file.
    CONF_String(module_output, "");
    // memory_limiation_per_thread_for_schema_change unit GB
    CONF_Int32(memory_limiation_per_thread_for_schema_change, "2");

    CONF_Int64(max_unpacked_row_block_size, "104857600");

    CONF_Int32(file_descriptor_cache_clean_interval, "3600");
    CONF_Int32(base_expansion_trigger_interval, "1");
    CONF_Int32(cumulative_check_interval, "1");
    CONF_Int32(disk_stat_monitor_interval, "5");
    CONF_Int32(unused_index_monitor_interval, "30");
    CONF_String(storage_root_path, "${PALO_HOME}/storage");
    CONF_Int32(min_percentage_of_error_disk, "50");
    CONF_Int32(default_num_rows_per_data_block, "1024");
    CONF_Int32(default_num_rows_per_column_file_block, "1024");
    CONF_Int32(max_tablet_num_per_shard, "1024");
    // garbage sweep policy
    CONF_Int32(max_garbage_sweep_interval, "86400");
    CONF_Int32(min_garbage_sweep_interval, "200");
    CONF_Int32(snapshot_expire_time_sec, "864000");
    // 仅仅是建议值，当磁盘空间不足时，trash下的文件保存期可不遵守这个参数
    CONF_Int32(trash_file_expire_time_sec, "259200");
    CONF_Int32(disk_capacity_insufficient_percentage, "90");
    // check row nums for BE/CE and schema change. true is open, false is closed.
    CONF_Bool(row_nums_check, "true")
    // be policy
    CONF_Int32(base_expansion_thread_num, "1");
    CONF_Int64(be_policy_start_time, "20");
    CONF_Int64(be_policy_end_time, "7");
    //file descriptors cache, by default, cache 30720 descriptors
    CONF_Int32(file_descriptor_cache_capacity, "30720");
    CONF_Int64(index_stream_cache_capacity, "10737418240");
    CONF_Int64(max_packed_row_block_size, "20971520");
    CONF_Int32(cumulative_write_mbytes_per_sec, "100");
    CONF_Int64(ce_policy_delta_files_number, "5");
    // ce policy: max delta file's size unit:B
    CONF_Int32(cumulative_thread_num, "1");
    CONF_Int64(ce_policy_max_delta_file_size, "104857600");
    CONF_Int64(be_policy_cumulative_files_number, "5");
    CONF_Double(be_policy_cumulative_base_ratio, "0.3");
    CONF_Int64(be_policy_be_interval_seconds, "604800");
    CONF_Int32(cumulative_source_overflow_ratio, "5");
    CONF_Int32(delete_delta_expire_time, "1440");
    // Port to start debug webserver on
    CONF_Int32(webserver_port, "8040");
    // Interface to start debug webserver on. If blank, webserver binds to 0.0.0.0
    CONF_String(webserver_interface, "");
    CONF_String(webserver_doc_root, "${PALO_HOME}");
    // If true, webserver may serve static files from the webserver_doc_root
    CONF_Bool(enable_webserver_doc_root, "true");
    // The number of times to retry connecting to an RPC server. If zero or less,
    // connections will be retried until successful
    CONF_Int32(rpc_retry_times, "10");
    // The interval, in ms, between retrying connections to an RPC server
    CONF_Int32(rpc_retry_interval_ms, "30000");
    //reactor number
    CONF_Int32(rpc_reactor_threads, "10")
    // Period to update rate counters and sampling counters in ms.
    CONF_Int32(periodic_counter_update_period_ms, "500");

    // Used for mini Load
    CONF_Int64(load_data_reserve_hours, "24");
    CONF_Int64(mini_load_max_mb, "2048");

    // Fragment thread pool
    CONF_Int32(fragment_pool_thread_num, "64");
    CONF_Int32(fragment_pool_queue_size, "1024");

    //for cast
    CONF_Bool(cast, "true");

    // Spill to disk when query
    // Writable scratch directories, splitted by ";"
    CONF_String(query_scratch_dirs, "${PALO_HOME}");

    // Control the number of disks on the machine.  If 0, this comes from the system settings.
    CONF_Int32(num_disks, "0");
    // The maximum number of the threads per disk is also the max queue depth per disk.
    CONF_Int32(num_threads_per_disk, "0");
    // The read size is the size of the reads sent to os.
    // There is a trade off of latency and throughout, trying to keep disks busy but
    // not introduce seeks.  The literature seems to agree that with 8 MB reads, random
    // io and sequential io perform similarly.
    CONF_Int32(read_size, "8388608"); // 8 * 1024 * 1024, Read Size (in bytes)
    CONF_Int32(min_buffer_size, "1024"); // 1024, The minimum read buffer size (in bytes)

    // For each io buffer size, the maximum number of buffers the IoMgr will hold onto
    // With 1024B through 8MB buffers, this is up to ~2GB of buffers.
    CONF_Int32(max_free_io_buffers, "128");

    CONF_Bool(disable_mem_pools, "false");

    // The probing algorithm of partitioned hash table.
    // Enable quadratic probing hash table
    CONF_Bool(enable_quadratic_probing, "false");

    // for pprof
    CONF_String(pprof_profile_dir, "${PALO_HOME}/log")

    // for partition
    CONF_Bool(enable_partitioned_hash_join, "false")
    CONF_Bool(enable_partitioned_aggregation, "false")

    // for kudu
    // "The maximum size of the row batch queue, for Kudu scanners."
    CONF_Int32(kudu_max_row_batches, "0")
    // "The period at which Kudu Scanners should send keep-alive requests to the tablet "
    // "server to ensure that scanners do not time out.")
    // 150 * 1000 * 1000
    CONF_Int32(kudu_scanner_keep_alive_period_us, "15000000")

    // "(Advanced) Sets the Kudu scan ReadMode. "
    // "Supported Kudu read modes are READ_LATEST and READ_AT_SNAPSHOT. Invalid values "
    // "result in using READ_LATEST."
    CONF_String(kudu_read_mode, "READ_LATEST")
    // "Whether to pick only leader replicas, for tests purposes only.")
    CONF_Bool(pick_only_leaders_for_tests, "false")
    // "The period at which Kudu Scanners should send keep-alive requests to the tablet "
    // "server to ensure that scanners do not time out."
    CONF_Int32(kudu_scanner_keep_alive_period_sec, "15")
    CONF_Int32(kudu_operation_timeout_ms, "5000")
    // "If true, Kudu features will be disabled."
    CONF_Bool(disable_kudu, "false")

} // namespace config

} // namespace palo

#endif // BDG_PALO_BE_SRC_COMMON_CONFIG_H

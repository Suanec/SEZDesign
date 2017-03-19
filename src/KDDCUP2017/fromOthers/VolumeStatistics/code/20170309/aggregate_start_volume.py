# -*- coding: utf-8 -*-
#!/usr/bin/env python

"""
Objective:
Calculate the average travel time for each 20-minute time window.

"""

# import necessary modules
import math
from datetime import datetime,timedelta

file_suffix = '.csv'
path = '../'  # set the data directory

def avgTravelTime(in_file):

    out_suffix = '_20min_avg_start_volume_15_17'
    in_file_name = '../data/datasets/training/' + in_file + file_suffix
    out_file_name = '../../data/datasets/training/' + in_file.split('_')[1] + out_suffix + file_suffix

    # Step 1: Load trajectories
    fr = open(path + in_file_name, 'r')
    fr.readline()  # skip the header
    traj_data = fr.readlines()
    fr.close()
    # print(traj_data[0])

    # Step 2: Create a dictionary to store travel time for each route per time window
    start_volumes = dict()  # key: intersection_id. Value is also a dictionary of which key is the start time for the time window and value is a list of travel times
    for i in range(len(traj_data)):
        #print(traj_data[i].strip())
        each_traj = traj_data[i].replace('"', '').split(',')
        intersection_id = each_traj[0]
        if intersection_id not in start_volumes:
            start_volumes[intersection_id] = dict()

        trace_start_time = each_traj[3]
        trace_start_time = datetime.strptime(trace_start_time, "%Y-%m-%d %H:%M:%S")
        time_window_minute = int(math.floor(trace_start_time.minute / 20) * 20)
        start_time_window = datetime(trace_start_time.year, trace_start_time.month, trace_start_time.day,
                                     trace_start_time.hour, time_window_minute, 0)
        if trace_start_time.hour >= 15 and trace_start_time.hour <= 16:
            # print start_time_window
            tt = float(each_traj[-1]) # travel time

            if start_time_window not in start_volumes[intersection_id]:
                start_volumes[intersection_id][start_time_window] = 0
            start_volumes[intersection_id][start_time_window] += 1
            # print start_volumes[intersection_id][start_time_window]

    # Step 3: Calculate average travel time for each route per time window
    fw = open(out_file_name, 'w')
    fw.writelines(','.join(['"intersection_id"', '"time_window"', '"start_volumes"']) + '\n')
    for intersection_id in start_volumes:
        route_time_windows = list(start_volumes[intersection_id].keys())
        route_time_windows.sort()
        for time_window_start in route_time_windows:
            time_window_end = time_window_start + timedelta(minutes=20)
            volume = start_volumes[intersection_id][time_window_start]
            out_line = ','.join(['"' + intersection_id + '"',
                                 '"[' + str(time_window_start) + ',' + str(time_window_end) + ')"',
                                 '"' + str(volume) + '"']) + '\n'
            fw.writelines(out_line)
    fw.close()

def main():

    in_file = 'trajectories(table 5)_training'
    avgTravelTime(in_file)

if __name__ == '__main__':
    main()




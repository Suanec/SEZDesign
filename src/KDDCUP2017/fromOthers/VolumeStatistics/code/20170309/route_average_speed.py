# -*- coding: utf-8 -*-
#!/usr/bin/env python

"""
Objective:
Calculate the average travel time for each 20-minute time window.

"""

# import necessary modules
import math
import pandas
import time
import re
from datetime import datetime, timedelta


file_suffix = '.csv'
path = '../'  # set the data directory


def get_link_length(in_file):
    in_file_name = '../../data/datasets/training/' + in_file + file_suffix
    link_infos = pandas.read_csv(in_file_name)
    link_length = {}
    for link_id, length in link_infos.loc[:, ['link_id', 'length']].values:
        link_length[link_id] = length
        
    return link_length


def vehicle_average_speed(in_file, link_length):
    in_file_name = '../../data/datasets/training/' + in_file + file_suffix
    out_file_name = '../../data/datasets/training/' + in_file.split('_')[1] + '_vehicle_speed' + file_suffix
    # Step 1: Load trajectories
    traj_data = pandas.read_csv(in_file_name)
    
    # Step 2: Create a dictionary to store travel time for each route per time window
    vehicle_speed = []
    for info in traj_data.values:
        [intersection_id, tollgate_id, vehicle_id, starting_time, travel_seq, avg_time] = info
        speed_seq, avg_speed, n = [], 0.0, 0
        for seq in travel_seq.split(';'):
            [link_id, start_time, spend_time] = seq.split('#')
            speed = float(spend_time) / link_length[int(link_id)]
            avg_speed += speed
            n += 1
            speed_seq.append('#'.join([link_id, str(speed)]))
        speed_seq = ';'.join(speed_seq)
        avg_speed = 1.0 * avg_speed / n
        vehicle_speed.append([intersection_id, tollgate_id, vehicle_id, starting_time, speed_seq, avg_speed])
    vehicle_speed = pandas.DataFrame(vehicle_speed, columns=['intersection_id', 'tollgate_id', 'vehicle_id', 'starting_time', 'speed_seq', 'avg_speed'])
    vehicle_speed.to_csv(out_file_name, index=False)


def route_average_speed(in_file, link_length):
    in_file_name = '../../data/datasets/training/' + in_file + file_suffix
    out_file_name = '../../data/datasets/training/' + in_file.split('_')[1] + '_20min_route_avg_speed' + file_suffix
    # Step 1: Load trajectories
    traj_data = pandas.read_csv(in_file_name)
    
    # Step 2: Create a dictionary to store travel time for each route per time window
    route_avg_speed = {}
    for info in traj_data.values:
        [intersection_id, tollgate_id, vehicle_id, starting_time, travel_seq, avg_time] = info
        
        route = intersection_id + '_' + str(tollgate_id)
        if route not in route_avg_speed: 
            route_avg_speed[route] = {}
        
        avg_speed, n = 0.0, 0
        for seq in travel_seq.split(';'):
            [link_id, start_time, spend_time] = seq.split('#')
            speed = float(spend_time) / link_length[int(link_id)]
            avg_speed += speed
            n += 1
        avg_speed = 1.0 * avg_speed / n
            
        trace_start_time = datetime.strptime(starting_time, "%Y-%m-%d %H:%M:%S")
        time_window_minute = int(math.floor(trace_start_time.minute / 20) * 20)
        start_time_window = datetime(trace_start_time.year, trace_start_time.month, trace_start_time.day,
                                     trace_start_time.hour, time_window_minute, 0)
        if start_time_window not in route_avg_speed:
            route_avg_speed[route][start_time_window] = list()
        route_avg_speed[route][start_time_window].append(avg_speed)
    
    route_avg_speed_list = list()
    for route in route_avg_speed:
        [intersection_id, tollgate_id] = route.split('_')
        route_time_windows = list(route_avg_speed[route].keys())
        route_time_windows.sort()
        for time_window_start in route_time_windows:
            avg_speed = route_avg_speed[route][time_window_start]
            avg_speed = 1.0 * sum(avg_speed) / len(avg_speed)
            time_window_end = time_window_start + timedelta(minutes=20)
            time_window = '[' + str(time_window_start) + ', ' + str(time_window_end) + ')'
            route_avg_speed_list.append([intersection_id, tollgate_id, time_window, avg_speed])
            
    route_avg_speed_list = pandas.DataFrame(route_avg_speed_list, columns=['intersection_id', 'tollgate_id', 'time_window', 'avg_speed'])
    route_avg_speed_list.to_csv(out_file_name, index=False)


def main():

    link_length = get_link_length('links (table 3)')
    vehicle_average_speed('trajectories(table 5)_training', link_length)
    # route_average_speed('trajectories(table 5)_training', link_length)

if __name__ == '__main__':
    main()




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
from numpy import corrcoef
import matplotlib.pyplot as plt


file_suffix = '.csv'


def set_free_time(in_file):
    in_file_name = '../../data/datasets/training/' + in_file + file_suffix
    out_file_name = '../../data/datasets/preprocessing/' + 'training_hour_volume' + file_suffix
    
    traj_data = pandas.read_csv(in_file_name)

    volumes = {}
    for info in traj_data[traj_data.direction == 0].values:
        [pass_time, tollgate_id, direction, model, has_ect, type] = info
        pass_time = datetime.strptime(pass_time, "%Y-%m-%d %H:%M:%S")
        each_date = datetime(pass_time.year, pass_time.month, pass_time.day, 0, 0, 0)
        start_time_window = datetime(pass_time.year, pass_time.month, pass_time.day, \
                                     pass_time.hour, 0, 0)
        # free time is 2:00~5:00
        if start_time_window.hour >= 2 and start_time_window.hour <= 4:
            if each_date not in volumes:
                volumes[each_date] = {}
            if start_time_window not in volumes[each_date]:
                volumes[each_date][start_time_window] = {}
            if tollgate_id not in volumes[each_date][start_time_window]:
                volumes[each_date][start_time_window][tollgate_id] = 0
            volumes[each_date][start_time_window][tollgate_id] += 1
    volumes_list = []
    for each_date in volumes:
        free = True
        for start_time_window in volumes[each_date]:
            for tollgate_id in volumes[each_date][start_time_window]:
                if volumes[each_date][start_time_window][tollgate_id] > 45:
                    free = False
                volumes_list.append([each_date, start_time_window, tollgate_id, \
                                     volumes[each_date][start_time_window][tollgate_id]])
        if free:
            print each_date
    volumes_list = pandas.DataFrame(volumes_list, columns=['date_time', 'start_time_window', 'tollgate_id', 'volume'])
    volumes_list.to_csv(out_file_name, index=False)
    
    
def get_free_time(in_file):
    in_file_name = '../../data/datasets/preprocessing/' + in_file + file_suffix
    
    free_time = pandas.read_csv(in_file_name)
    
    free_time_date = {}
    for date in free_time.free_time_date:
        date = datetime.strptime(date, "%Y/%m/%d")
        free_time_date[date] = None
    
    return free_time_date


def get_link_length(in_file):
    in_file_name = '../../data/datasets/training/' + in_file + file_suffix
    link_infos = pandas.read_csv(in_file_name)
    link_length, link_lanes = {}, {}
    for link_id, length, lanes in link_infos.loc[:, ['link_id', 'length', 'lanes']].values:
        link_length[link_id] = length
        link_lanes[link_id] = lanes
        
    return link_length, link_lanes


def vehicle_freetime_speed(in_file, link_length):
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


def route_average_speed(in_file, link_length, link_lanes, free_date):
    in_file_name = '../../data/datasets/training/' + in_file + file_suffix
    out_file_name1 = '../../data/datasets/preprocessing/' + 'freetime_vehicle_speed' + file_suffix
    out_file_name2 = '../../data/datasets/preprocessing/' + 'speed_lanes_correaltion' + file_suffix
    # Step 1: Load trajectories
    traj_data = pandas.read_csv(in_file_name)
    
    # Step 2: Create a dictionary to store travel time for each route per time window
    vehicle_speed, speed_lanes_corr = [], {}
    for info in traj_data.values:
        [intersection_id, tollgate_id, vehicle_id, starting_time, travel_seq, avg_time] = info
        
        trace_start_time = datetime.strptime(starting_time, "%Y-%m-%d %H:%M:%S")
        today_date = datetime(trace_start_time.year, trace_start_time.month, trace_start_time.day, \
                              trace_start_time.hour, 0, 0)
         
        # if today_date in free_date and trace_start_time.hour == 3:    
                    
        if vehicle_id not in speed_lanes_corr:
            speed_lanes_corr[vehicle_id] = {}
        if today_date not in speed_lanes_corr[vehicle_id]:
            speed_lanes_corr[vehicle_id][today_date] = []
            
        for seq in travel_seq.split(';'):
            [link_id, start_time, spend_time] = seq.split('#')
            lanes = link_lanes[int(link_id)]
            start_time = datetime.strptime(start_time, "%Y-%m-%d %H:%M:%S")
            speed = link_length[int(link_id)] / float(spend_time)
            vehicle_speed.append([link_id, lanes, vehicle_id, start_time, speed])
            
            # speed and lanes correlation
            speed_lanes_corr[vehicle_id][today_date].append([speed, lanes])
                
    vehicle_speed = pandas.DataFrame(vehicle_speed, columns=['link_id', 'lanes', 'vehicle_id', 'start_time', 'speed'])
    vehicle_speed.to_csv(out_file_name1, index=False)
    
    # speed and lanes correlation
    speed_lanes_corr_list = list()
    for vehicle_id in speed_lanes_corr:
        for today_date in speed_lanes_corr[vehicle_id]:
            speeds, lanes = zip(*speed_lanes_corr[vehicle_id][today_date])
            speed_lanes_corr_list.append([vehicle_id, today_date, corrcoef(speeds, lanes)[0,1]])
                
    speed_lanes_corr_list = pandas.DataFrame(speed_lanes_corr_list, columns=['vehicle_id', 'start_time', 'correlation'])
    speed_lanes_corr_list.to_csv(out_file_name2, index=False)


def plot_route_speed_incdec(in_file, link_length):
    in_file_name = '../../data/datasets/training/' + in_file + file_suffix
    # Step 1: Load trajectories
    traj_data = pandas.read_csv(in_file_name)
    
    # Step 2: Create a dictionary to store travel time for each route per time window
    speed_incdec = []
    fig = plt.figure()
    
    for info in traj_data[0:10000].values:
        [intersection_id, tollgate_id, vehicle_id, starting_time, travel_seq, avg_time] = info
        
        route = str(intersection_id) + '-' + str(tollgate_id)
        if route == 'C-3':
            
            speeds = list()
            for seq in travel_seq.split(';'):
                [link_id, start_time, spend_time] = seq.split('#')
                speed = float(spend_time) / link_length[int(link_id)]
                speeds.append(speed)
            incdec = [second - first for first, second in zip(speeds[:-1], speeds[1:])]
            plt.plot(range(len(incdec)), incdec, 'o', color='#7D9EC0', markersize=10.0, alpha=0.01)
    
    plt.axis([-1, len(incdec), -0.5, 0.5])
    plt.show()


def main():

    # set_free_time('volume(table 6)_training')
    free_date = get_free_time('free_time_date')
    link_length, link_lanes = get_link_length('links (table 3)')
    # route_average_speed('trajectories(table 5)_training', link_length, link_lanes, free_date)
    plot_route_speed_incdec('trajectories(table 5)_training', link_length)


if __name__ == '__main__':
    main()




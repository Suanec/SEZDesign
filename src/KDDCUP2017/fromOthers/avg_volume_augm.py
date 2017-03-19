import math
from datetime import datetime,timedelta


file_suffix = '.csv'
path = './data/'  # set the data directory
in_file = 'volume(table 6)_training'
# avgVolume(in_file)
out_suffix = '_20min_avg_volume_augm'
in_file_name = in_file + file_suffix
out_file_name = path + in_file.split('_')[1] + out_suffix + file_suffix

# Step 1: Load volume data
fr = open(path + in_file_name, 'r')
fr.readline()  # skip the header
vol_data = fr.readlines()
fr.close()

# Step 2: Create a dictionary to caculate and store volume per time window
volumes = {}  # key: time window value: dictionary
for i in range(len(vol_data)):
    for bias in range(0, 20):
        each_pass = vol_data[i].replace('"', '').split(',')
        tollgate_id = each_pass[1]
        direction = each_pass[2]

        pass_time = each_pass[0]
        pass_time = datetime.strptime(pass_time, "%Y-%m-%d %H:%M:%S")

        time_window_minute = bias if (pass_time.minute >= 0 + bias and pass_time.minute < 20 + bias) \
        else bias + 20 if (pass_time.minute >= 20 + bias and pass_time.minute < 40 + bias) \
        else bias + 40

        #time_window_minute = int(math.floor((pass_time.minute - bias + 20)/ 20 - 1) * 20 + bias)

        #print pass_time
        start_time_window = datetime(pass_time.year, pass_time.month, pass_time.day,
                                     pass_time.hour, time_window_minute, 0)

        if start_time_window not in volumes:
            volumes[start_time_window] = {}
        if tollgate_id not in volumes[start_time_window]:
            volumes[start_time_window][tollgate_id] = {}
        if direction not in volumes[start_time_window][tollgate_id]:
            volumes[start_time_window][tollgate_id][direction] = 1
        else:
            volumes[start_time_window][tollgate_id][direction] += 1


# Step 3: format output for tollgate and direction per time window
fw = open(out_file_name, 'w')
fw.writelines(','.join(['"tollgate_id"', '"time_window"', '"direction"', '"volume"']) + '\n')
time_windows = list(volumes.keys())
time_windows.sort()
for time_window_start in time_windows:
    time_window_end = time_window_start + timedelta(minutes=20)
    for tollgate_id in volumes[time_window_start]:
        for direction in volumes[time_window_start][tollgate_id]:
           out_line = ','.join(['"' + str(tollgate_id) + '"', 
                       '"[' + str(time_window_start) + ',' + str(time_window_end) + ')"',
                             '"' + str(direction) + '"',
                             '"' + str(volumes[time_window_start][tollgate_id][direction]) + '"',
                           ]) + '\n'
           fw.writelines(out_line)
fw.close()



import datetime

atime = "2017-03-07 17:22:58"
btime = "2017-03-07 17:53:22"
timePattern = "%Y-%m-%d %H:%M:%S"

def str2time (str) :
  return datetime.datetime.strptime(str, timePattern)

def time2str (time) :
  return time.strftime(timePattern)

def calcTimeMinus( timeStr1, timeStr2 ) : 
  return (str2time(timeStr2) - str2time(timeStr1))

def timeStampParser( timeStamp ) : 
  return time.strftime(timePattern, time.localtime(timeStamp))

def timeAddMinutes( timeStr1, minutes = 20 ) : 
  base = time.mktime(str2time(timeStr1).timetuple())
  added = datetime.timedelta(0,minutes * 60).seconds
  result = base + added
  return timeStampParser(result)




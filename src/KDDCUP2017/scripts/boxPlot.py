import matplotlib.pyplot as plt
import pandas as pd
# import seaborn as sns
import numpy as np

df = pd.read_excel('nodeTravelCost.xlsx', 'Sheet1')

# 1、直方图
fig = plt.figure()
ax = fig.add_subplot(111)
ax.hist(df, bins=123-100)
plt.title('ID')
plt.xlabel('ID')
plt.ylabel('count')
plt.show()


# 2、箱线图  
fig = plt.figure()
ax = fig.add_subplot(111)
ax.boxplot(df[111].head(10000),df[100].head(10000))
plt.show()
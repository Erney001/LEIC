import matplotlib.pyplot as plt

l1 = (0.0305*100, 0.0363*100, 0.0770*100, 0.1181*100) # confirmar terceiro valor
l11 = []
l2 = (0.1247*100, 0.1184*100, 0.1503*100, 0.2021*100)
l22 = []
l3 = (0.1960*100, 0.1829*100, 0.2288*100, 0.3340*100)
l33 = []
ax = (8, 16, 32, 64)

for i in l1:
    l11 += [i * (9/1000)]
for i in l2:
    l22 += [i * (4.5/1000)]
for i in l3:
    l33 += [i * (2.25/1000)]

#plt.plot(ax, l11, color='blue', label='1kb')
#plt.plot(ax, l22, color='green', label='512b')
#plt.plot(ax, l33, color='red', label='256b')
#plt.legend(loc="upper left")
#plt.title("Miss rate % em função do tamanho do bloco para cada tamanho\nda cache considerando o custo da cache")
#plt.xticks(ax, ax)
#plt.show()


#compulsory, capacity, conflict config1
co = (0.0254*100, 0.0216*100, 0.2859*100, 0.2876*100)
cap = (0.0561*100, 0.0488*100, 0.7059*100, 0.7080*100)
con = (0.9186*100, 0.9295*100, 0.0082*100, 0.0044*100)
mr = (0.0305*100, 0.0357*100, 0.0027*100, 0.0027*100)
axx = (1, 2, 4, 8)

#compulsory, capacity, conflict config2
co2 = (0.0106*100, 0.0106*100, 0.2444*100, 0.2413*100)
cap2 = (0.0303*100, 0.0329*100, 0.7556*100, 0.7587*100)
con2 = (0.9591*100, 0.9565*100, 0.00*100, 0.00*100)
mr2 = (0.0363*100, 0.0364*100, 0.0016*100, 0.0016*100)

#plt.style.use('seaborn-dark-palette')
#fig, ax1 = plt.subplots()

#ax1.plot(axx, co2, color='green', label='Compulsory misses')
#ax1.plot(axx, cap2, color='red', label='Capacity misses')
#ax1.plot(axx, con2, color='orange', label='Conflict misses')

#ax1.legend(loc="upper right")
#plt.xticks(axx, axx)
#plt.title("L1 Config 2")

#ax1.set_ylabel('3 Cs Miss rate %', color='black')

#ax2 = ax1.twinx()
#ax2.set_ylabel('Miss rate %', color='darkblue')
#ax2.plot(axx, mr, color='darkblue', label='Total miss rate')
#ax2.tick_params(axis='y', labelcolor='darkblue')
#ax2.legend(loc="right")

#plt.show()


mr_l2 = (0.4773*100, 0.2386*100, 0.1193*100, 0.0598*100)
bl_size = (16, 32, 64, 128)

#plt.plot(bl_size, mr_l2, color='blue')
#plt.title("Miss rate % em função do tamanho do bloco para a cache L2")
#plt.xticks(bl_size, bl_size)
#plt.show()


ccc_l2 = (0.3741*100, 0.6259*100, 0.000*100, 0.0598*100)
a = [1, 2, 3, 4]

fig, ax1 = plt.subplots()

ax1.plot(a[0], ccc_l2[0], marker="x", markersize=5, markerfacecolor="green",  markeredgecolor="green", label='Compulsory')
ax1.plot(a[1], ccc_l2[1], marker="x", markersize=5, markerfacecolor="red",  markeredgecolor="red", label='Capacity')
ax1.plot(a[2], ccc_l2[2], marker="x", markersize=5, markerfacecolor="orange",  markeredgecolor="orange", label='Conflict')

ax1.legend(loc="center")
plt.xticks(a, a)
plt.title("3Cs misses fraction na cache L2 & Total Miss Rate")

ax1.set_ylabel('3 Cs Miss fraction', color='black')

ax2 = ax1.twinx()
ax2.set_ylabel('Miss rate %', color='darkblue')
ax2.plot(a[3], ccc_l2[3], marker="o", markersize=5, markerfacecolor="darkblue", label='Total Miss Rate')
ax2.tick_params(axis='y', labelcolor='darkblue')
ax2.legend(loc="upper right")

plt.show()

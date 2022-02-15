from scipy import stats

acc_m1 = (0.7, 0.5, 0.55, 0.55, 0.6)
acc_m2 = (0.75, 0.6, 0.6, 0.65, 0.55)

print(stats.ttest_rel(acc_m1, acc_m2))

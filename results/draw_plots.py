import matplotlib.pyplot as plt
import pandas as pd

archs = ["TCP_ASYNCHRONOUS", "TCP_NON_BLOCKING", "TCP_SINGLE_THREAD", "TCP_THREAD_POOLED",
         "UDP_SINGLE_THREAD", "UDP_THREADPOOLED"]
params = ["ELEMENTS_PER_REQ", "CLIENTS_PARALLEL", "TIME_DELTA"]

def make_patch_spines_invisible(ax):
    ax.patch.set_visible(False)
    for sp in ax.spines.values():
        sp.set_visible(False)
    ax.tick_params(labeltop='off', labelbottom='off', labelleft='off', labelright='off')
    ax.tick_params(top='off', bottom='off', left='off', right='off')

def name_col(fig, name, i):
    a = fig.add_subplot(1,3,i)
    make_patch_spines_invisible(a)
    a.set_title(name, y=-0.1)

def name_row(fig, name, i):
    a = fig.add_subplot(3,1,i)
    make_patch_spines_invisible(a)
    a.set_title(name, y=1.1)

def plot_benchmark_results(arch):
    csv = pd.read_csv(arch + ".csv", delimiter=" ")
    mcsv = csv[csv.SERVER_ARCH == arch]

    fig = plt.figure()
    ax = fig.add_subplot(111)
    make_patch_spines_invisible(ax)

    ax.set_ylabel('Time in ns')
    for i, name in enumerate(('CLIENT_TIME', 'QUERY_PROC_TIME', 'CONNECT_PROC_TIME')):
        i += 1
        name_col(fig, name, i)

    for i, name in enumerate(("ELEMENTS_PER_REQ", "CLIENTS_PARALLEL", "TIME_DELTA")):
        i += 1
        name_row(fig, name, i)

    i = 0
    for param in ["ELEMENTS_PER_REQ", "CLIENTS_PARALLEL"]:
        mmcsv = mcsv[mcsv.PARAM == param]
        x = mmcsv.PARAM_VAL

        s1 = mmcsv.CLIENT_TIME
        s2 = mmcsv.QUERY_PROC_TIME
        s3 = mmcsv.CONNECT_PROC_TIME

        ax1 = fig.add_subplot(3,3,i+1)
        ax1.plot(x, s1)
        ax1.tick_params(labelbottom='off', bottom='off')

        ax2 = fig.add_subplot(3,3,i+2)
        ax2.tick_params(labelbottom='off', bottom='off')
        ax2.plot(x, s2)

        ax3 = fig.add_subplot(3,3,i+3)
        ax3.tick_params(labelbottom='off', bottom='off')
        ax3.plot(x, s3)

        i += 3

    mmcsv = mcsv[mcsv.PARAM == "TIME_DELTA"]
    x = mmcsv.PARAM_VAL

    s1 = mmcsv.CLIENT_TIME
    s2 = mmcsv.QUERY_PROC_TIME
    s3 = mmcsv.CONNECT_PROC_TIME

    ax1 = fig.add_subplot(3,3,i+1)
    ax1.plot(x, s1)

    ax2 = fig.add_subplot(3,3,i+2)
    ax2.plot(x, s2)

    ax3 = fig.add_subplot(3,3,i+3)
    ax3.plot(x, s3)

    fig.tight_layout(w_pad=1, h_pad=0.5)
    fig.set_size_inches(10, 10)
    fig.savefig("../plots/" + arch + ".png", dpi=100)

def main():
    for arch in archs:
        plot_benchmark_results(arch)

if __name__ == "__main__":
    main()
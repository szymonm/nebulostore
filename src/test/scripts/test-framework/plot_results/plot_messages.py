# -*- coding:utf-8 -*-

import plot_commons as commons
import numpy as np
import matplotlib.pyplot as plt


results_dir = "../results/"
all_file = results_dir + "all.txt"


def plot_messages():

    all_phases = set()
    all_peers = set()
    all_msg_mult = set()

    msg_results = {}

    commons.read_file(all_file, msg_results, all_phases, all_peers, all_msg_mult)

    msg_stats = commons.calc_stats(msg_results)

    print msg_stats
  
    pick_phases = 10.0
    msg_stats = dict(filter(lambda ((phases, a, b), c) : phases == pick_phases, msg_stats.iteritems()))

    # converting stats - recalculating messages sent
    msg_stats = dict(map(lambda ((a, peers, mult), c) : ((a, peers, 5.0*mult), c), msg_stats.iteritems()))

    print msg_stats

    # series of plots.
    peers_set = set(map(lambda (a, peers, b): peers, msg_stats.keys()))
    peers_set = list(peers_set)
    peers_set.sort()

    cols = 1

    fig, axs = plt.subplots(nrows = (len(peers_set) + len(peers_set)%cols)/cols, ncols = cols)
    i = 0
    for peers in peers_set:
        print "\nSubplot", i, " on ", i/cols, i%cols

        ax = axs[i/cols] # , i % cols]
        ax_lost = ax.twinx()

        msgs_list = map(lambda (a,b, msgs) : msgs, filter(lambda (a, p, c) : p == peers, msg_stats))
        msgs_list.sort()
        x = np.array(msgs_list)

        print "x: ", x

        time_err = []
        for msgs in msgs_list:
            time_err.append(msg_stats[(pick_phases, peers, msgs)][0])            

        y = np.array(map(lambda (a,b): a, time_err))
        err = np.array(map(lambda (a,b): b, time_err))

        print "y: ", y
        print "err: ", err

        times,a,b, = ax.errorbar(x, y, yerr = err, fmt = 'o-', label = "Time")
        ax.set_title("Messages exchange time for " + str(int(peers)) + " peers")
        ax.set_ylabel("Time in ms")
        ax.set_xlabel("Number of messages sent by a single peer per phase")
        i += 1

        lost_err = []

        for msgs in msgs_list:
            lost_err.append(msg_stats[(pick_phases, peers, msgs)][1])


        y = np.array(map(lambda (a,b): a, lost_err))
        err = np.array(map(lambda (a,b): b, lost_err))

        lost,a,b, = ax_lost.errorbar(x, y, yerr = err, fmt = 'o-', color = "r", linestyle="--", label = "Lost messages")
        ax_lost.set_ylabel("Percent of lost messages")

        ax.legend([times, lost], [times.get_label(), lost.get_label()], loc=2)



    fig.suptitle("Messages exchange time in setups of different numbers of peers")
    plt.show()

    # First plot accomplished


    



if __name__ == "__main__":
    plot_messages()

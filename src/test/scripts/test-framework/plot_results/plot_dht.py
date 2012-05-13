# -*- coding:utf-8 -*-

import plot_commons as commons
import numpy as np
import matplotlib.pyplot as plt


results_dir = "../results/"
dht_bdb = results_dir + "dht_bdb.txt"
dht_kad = results_dir + "dht_kad.txt"

def plot_dht():

    bdb_results = {}
    kad_results = {}

    all_phases = set()
    all_peers = set()
    all_keys_mult = set()

    commons.read_file(dht_bdb, bdb_results, all_phases, all_peers, all_keys_mult)
    commons.read_file(dht_kad, kad_results, all_phases, all_peers, all_keys_mult)


    bdb_stats = commons.calc_stats(bdb_results)
    kad_stats = commons.calc_stats(kad_results)

    print bdb_stats
    print kad_stats

    pick_phases = 10.0

    bdb_stats = dict(filter(lambda ((phases, a, b), c) : phases == pick_phases, bdb_stats.iteritems()))
    kad_stats = dict(filter(lambda ((phases, a, b), c) : phases == pick_phases, kad_stats.iteritems()))

    # Converting stats, recalculating operations issued
    bdb_stats = dict(map(lambda ((a, peers, mult), c) : ((a, peers, peers*mult), c), bdb_stats.iteritems()))
    kad_stats = dict(map(lambda ((a, peers, mult), c) : ((a, peers, peers*mult), c), kad_stats.iteritems()))

     # series of plots.
    peers_set = set(map(lambda (a, peers, b): peers, bdb_stats.keys()))
    peers_set = list(peers_set)
    peers_set.sort()

    fig, axs = plt.subplots(nrows = (len(peers_set) + len(peers_set)%2)/2, ncols = 2, sharex = True)
    i = 0
    for peers in peers_set:
        print "\nSubplot", i
       
        ax = axs[i/2 , i % 2]
        ax_lost = ax.twinx()

        msgs_list = map(lambda (a,b, msgs) : msgs, filter(lambda (a, p, c) : p == peers, bdb_stats))
        msgs_list.sort()
        x = np.array(msgs_list)

        print "x: ", x

        time_err_bdb = []
        for msgs in msgs_list:
            time_err_bdb.append(bdb_stats[(pick_phases, peers, msgs)][0])

        y = np.array(map(lambda (a,b): a, time_err_bdb))
        err = np.array(map(lambda (a,b): b, time_err_bdb))

        print "y: ", y
        print "err: ", err

        times_bdb,a,b, = ax.errorbar(x, y, yerr = err, fmt = 'o-', label = "Bdb time")

        time_err_kad = []
        for msgs in msgs_list:
            time_err_kad.append(kad_stats[(pick_phases, peers, msgs)][0])

        y = np.array(map(lambda (a,b): a, time_err_kad))
        err = np.array(map(lambda (a,b): b, time_err_kad))

        print "y: ", y
        print "err: ", err

        times_kad,a,b, = ax.errorbar(x, y, yerr = err, fmt = 's-', label = "Kademlia time")



        ax.set_title("Messages exchange time for " + str(int(peers)) + " peers")
        ax.set_ylabel("Time in ms")
        ax.set_xlabel("Number of DHT operations issued by a single peer")
        i += 1

        lost_err_bdb = []

        for msgs in msgs_list:
            lost_err_bdb.append(bdb_stats[(pick_phases, peers, msgs)][1])


        y = np.array(map(lambda (a,b): a, lost_err_bdb))
        err = np.array(map(lambda (a,b): b, lost_err_bdb))

        lost_bdb,a,b, = ax_lost.errorbar(x, y, yerr = err, fmt = 'o-', color = "r", linestyle="--", label = "Bdb errors")

        lost_err_kad = []

        for msgs in msgs_list:
            lost_err_kad.append(kad_stats[(pick_phases, peers, msgs)][1])

        y = np.array(map(lambda (a,b): a, lost_err_kad))
        err = np.array(map(lambda (a,b): b, lost_err_kad))

        lost_kad,a,b, = ax_lost.errorbar(x, y, yerr = err, fmt = 's-', color = "r", linestyle="--", label = "Kademlia errors")

        ax_lost.set_ylabel("Percent of errors")

        ax.legend([times_bdb, lost_bdb, times_kad, lost_kad], [times_bdb.get_label(), lost_bdb.get_label(), times_kad.get_label(), lost_kad.get_label()])


    fig.suptitle("Messages exchange time in setups of different numbers of peers")
    plt.show()

 


if __name__ == "__main__":
    plot_dht()


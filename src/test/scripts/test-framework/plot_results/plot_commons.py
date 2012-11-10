# -*- coding:utf-8 -*-

def calc_stats(results):

    ret = {}

    for k,v in results.iteritems():
        var = 0.0
        var_e = 0.0
        mean = 0.0
        mean_e = 0.0
        for time, error in v:
            mean += time
            mean_e += error
        mean /= float(len(v))
        mean_e /= float(len(v))
        for time, error in v:
            var += (time - mean)**2
            var_e += (error - mean_e)**2
        var = (var / float(len(v)))**(0.5)
        var_e = (var_e / float(len(v)))**(0.5)

        ret[k] = ((mean, var), (mean_e*100.0, var_e*100.0))
        
    return ret


def read_file(filename, results, all_phases, all_peers, all_keys_mult):
    with open(filename) as bdb:
        for line in bdb:
            (phases, peers, keys_mult, time, error_rate)  = map(lambda x: float(x), line.strip("\n").split("\t"))
            if not results.has_key((phases, peers, keys_mult)):
                results[(phases, peers, keys_mult)] = []
               
            results[(phases, peers, keys_mult)].append((float(time), error_rate))
            all_phases.add(phases)
            all_peers.add(peers)
            all_keys_mult.add(keys_mult)



# -*- coding:utf-8 -*-

import os
import random
from xml.dom.minidom import Document


def create_user(user, edges):
        
    doc = Document()
    peerData = doc.createElement("peerData")
    doc.appendChild(peerData)

    def create_element(parent, name, value, element_type, privacy, noise = ""):

        elt = doc.createElement(name)
        parent.appendChild(elt)
       
        v = doc.createElement("value")
        elt.appendChild(v)
        v.appendChild(doc.createTextNode(value))

        p = doc.createElement(privacy)
        elt.appendChild(p)

        t = doc.createElement(element_type)
        elt.appendChild(t)

        if noise != "":
            n = doc.createElement("noise")
            n.appendChild(doc.createTextNode(noise))
            elt.appendChild(n)


    # Adding person name
    create_element(peerData, "name", "user(" + user + ")", "string", "private-my")

    # Adding public person name
    create_element(peerData, "name-public", "user(" + user + ")", "string", "public-my")

    age = str(random.randint(10, 30))
    # Adding age
    create_element(peerData, "age", age, "integer", "private-conditional-my", "5")

    # Adding public age
    create_element(peerData, "age-public", age, "integer", "public-my")

    # Creating friends list
    friends = doc.createElement("friends")
    peerData.appendChild(friends)

    for edge in edges:
        create_element(friends, "friend", edge, "integer", "private-my", "100")

    # Creating likes
    likes_no = random.randint(0, 10)
    likes = doc.createElement("likes")

    peerData.appendChild(likes)
    for i in xrange(likes_no):
        create_element(likes, "like", str(random.randint(0, 1000)), "integer", "private-my", "100")
    
    # Adding sports
    create_element(peerData, "sports", str(random.randint(0, 10)), "integer", "private-conditional-my")

    return doc.toprettyxml(indent="    ")    
        
def parse_pajek(input, output_dir, out_filename):

    edges = []
    with open(input) as pajek_file:
        edges_mode = False
        for line in pajek_file:
            if edges_mode:
                edge_desc = filter(lambda l: l != "", line.split(" "))
                start = edge_desc[0]
                stop = edge_desc[1]
                edges.append((start, stop))
                edges.append((stop, start))
            else:
                if line.find("Edges") > 0:
                    edges_mode = True

    print edges
    vertices = set(reduce(lambda acc, (a,b): acc + [a] + [b], edges, []))
    print vertices
    
    final = {}
    for v in vertices:
        final[v] = set(map(lambda (a, b) : b, filter(lambda (a,b): a == v, edges)))

    for v in vertices:
        print v, final[v]
    
    for v in vertices:
        try:
            os.makedirs(output_dir + v)
        except OSError as e:
            if e.errno != 17:
                raise e
            
        with open(output_dir + v + "/" + out_filename, "w") as outFile:
            outFile.write(create_user(v, final[v]))


if __name__ == "__main__":
    out_dir = "/home/marcin/5ROK/praca/nebulostore/repo/nebulostore/trunk/resources/test/query/pajek-40/"
    parse_pajek("1.net", out_dir, "peerData.xml")


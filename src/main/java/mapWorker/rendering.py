# -*- coding: utf-8 -*-


import mapnik
import sys

from mapnik import GeoJSON, PostGIS

import ctypes


### Set up map and style rules

m = mapnik.Map(500,500)
m.background = mapnik.Color('white')

s2 = mapnik.Style()

r2 = mapnik.Rule()


line_symbolizer2 = mapnik.LineSymbolizer(mapnik.Color('#bbbb77'), 1)
r2.symbols.append(line_symbolizer2)

s2.rules.append(r2)

m.append_style('Highlights',s2)


### Set up map layers



query2 = '(SELECT * FROM planet_osm_roads) as routinglines'
datasource_line2 = PostGIS(host='localhost', dbname='osm', user='postgres', port=5436, table=query2, geometry_field='way')
layer3 = mapnik.Layer('test route')
layer3.datasource = datasource_line2
layer3.styles.append('Highlights')
m.layers.append(layer3)


m.zoom_all()


mapnik.render_to_file(m, './tmp/img' + sys.argv[1] + '.png', 'png') # save the image




exit()
# -*- coding: utf-8 -*-


"""
	This script will combine several PostGIS layers into a map and save it as a PNG file
	Layer 1: All lines in the database table planet_osm_line
	Layer 2: Highways from the same table
	Layer 3: Two lines filtered by osm_id from the same table
	Layer 4: A point symbolized by a finish line flag
"""

import mapnik
from mapnik import GeoJSON, PostGIS

import ctypes 


### Set up map and style rules

m = mapnik.Map(720,400)
m.background = mapnik.Color('white')

backgroundStyle = mapnik.Style()
highlightStyle = mapnik.Style()
backgroundRules = mapnik.Rule()
highlightRules = mapnik.Rule()

line_symbolizer = mapnik.LineSymbolizer(mapnik.Color('#777777'), 1)
line_symbolizer2 = mapnik.LineSymbolizer(mapnik.Color('#00ff00'), 5)
backgroundRules.symbols.append(line_symbolizer)
highlightRules.symbols.append(line_symbolizer2)

backgroundStyle.rules.append(backgroundRules)
highlightStyle.rules.append(highlightRules)
m.append_style('Style1', backgroundStyle)
m.append_style('Style2', highlightStyle)

### Set up map layers

dbparams = dict(port=5436, dbname='osm',table='planet_osm_line',user='postgres', host = 'localhost')
postgis = mapnik.PostGIS(**dbparams)
layer1 = mapnik.Layer('streets full')
layer1.datasource = postgis
layer1.styles.append('MyStyle')
m.layers.append(layer1)

query2 = """
(SELECT way FROM planet_osm_line
WHERE osm_id IN (
	SELECT osm_id FROM hh_2po_4pgr 
	WHERE id IN (
		SELECT id2 AS node FROM pgr_dijkstra('
			SELECT id AS id,
			 source::integer,
			 target::integer,
			 cost::double precision AS cost
			FROM hh_2po_4pgr',
		128, 5103, false, false) 
	)
)) as dijkstra
"""

datasource_line2 = PostGIS(host='localhost', dbname='directionmaps', user='postgres', password='postgres', table=query2, geometry_field='way', extent_from_subquery=True)
layer2 = mapnik.Layer('test route')
layer2.datasource = datasource_line2
layer2.styles.append('Style2')
m.layers.append(layer2)

m.zoom_to_box(layer2.envelope())

mapnik.render_to_file(m, 'from-postgis-with-pgrouting.png', 'png')

exit()
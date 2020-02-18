# wiki-graphs

Small research project with Scala to analyse Wikipedia articles using [GraphX](https://spark.apache.org/graphx/) and visualization of [Gephi](https://gephi.org/).

## Usage

```bash
$ sbt:WikiGraphs> run --input_path=<input_path> --reference_path=<reference_path>
```

Parameters:
- **input_path:** csv file containing filename;abspath data
- **reference_path:** wikipedia article data as csv ns;pageid;title


## TODO

- Map the connections to Graph.
- Count number of connections for any given article.
- Attempt naive approach to travelling salesman problem.
- Visualizations for the graph and other results.
  - Research available tools for visualization with Scala.

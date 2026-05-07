package org.ops4j.smile.op;

import java.util.ArrayList;
import java.util.List;

import org.ops4j.OpData;
import org.ops4j.base.BaseOp;
import org.ops4j.cli.OpCLI;
import org.ops4j.exception.OpsException;
import org.ops4j.inf.Op;
import org.ops4j.smile.util.SmileUtil;
import org.ops4j.util.JacksonUtil;

import com.google.auto.service.AutoService;

import lombok.Getter;
import lombok.Setter;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import smile.clustering.CLARANS;
import smile.clustering.DBSCAN;
import smile.clustering.DENCLUE;
import smile.clustering.DeterministicAnnealing;
import smile.clustering.GMeans;
import smile.clustering.HierarchicalClustering;
import smile.clustering.KMeans;
import smile.clustering.MEC;
import smile.clustering.SpectralClustering;
import smile.clustering.XMeans;
import smile.clustering.linkage.CompleteLinkage;
import smile.clustering.linkage.Linkage;
import smile.clustering.linkage.SingleLinkage;
import smile.clustering.linkage.UPGMALinkage;
import smile.clustering.linkage.UPGMCLinkage;
import smile.clustering.linkage.WPGMALinkage;
import smile.clustering.linkage.WPGMCLinkage;
import smile.clustering.linkage.WardLinkage;
import smile.math.MathEx;
import smile.math.distance.ChebyshevDistance;
import smile.math.distance.CorrelationDistance;
import smile.math.distance.Distance;
import smile.math.distance.EuclideanDistance;
import smile.math.distance.HammingDistance;
import smile.math.distance.JaccardDistance;
import smile.math.distance.JensenShannonDistance;
import smile.math.distance.ManhattanDistance;
import smile.math.distance.SparseChebyshevDistance;
import smile.math.distance.SparseEuclideanDistance;
import smile.math.distance.SparseManhattanDistance;

@AutoService(Op.class)
@Command(name = "smile-cluster", description = "Identify clustered data.")
public class Cluster extends BaseOp<Cluster>
{
  public enum DistanceType {
    CHEBYSHEV, CORRELATION, EUCLIDIAN, HAMMING, JACCARD, JENSEN_SHANNON,
    MANHATTAN, SPARSE_CHEBYSHEV, SPARSE_EUCLIDIAN, SPARSE_MANHATTAN

    // Not these, for now...
    // DYNAMIC_TIMEWARP, MINKOWSKI, MAHALANOBIS, LEE SPARSE_MINKOWSKI
  }

  public enum LinkageType {
    SINGLE, COMPLETE, WARD, UPGMA, UPGMC, WPGMA, WPGMC
  }

  @Parameters(index = "0", arity = "0..*",
      description = "An optional list of fields to use for clustering the "
          + "specified field.  It omitted, then every field other than the "
          + "cluster field itself.")
  private @Getter @Setter List<String> features;

  @ArgGroup(exclusive = true, multiplicity = "1")
  private ClusterConfig                clusterConfig;

  static class ClusterConfig
  {
    @ArgGroup(exclusive = false)
    KMeansConfig       kmeans;
    @ArgGroup(exclusive = false)
    XMeansConfig       xmeans;
    @ArgGroup(exclusive = false)
    GMeansConfig       gmeans;
    @ArgGroup(exclusive = false)
    DbScanConfig       dbscan;
    @ArgGroup(exclusive = false)
    DenClueConfig      denclue;
    @ArgGroup(exclusive = false)
    HierarchicalConfig hierarchical;
    @ArgGroup(exclusive = false)
    ClaransConfig      clarans;
    @ArgGroup(exclusive = false)
    AnnealingConfig    annealing;
    @ArgGroup(exclusive = false)
    SpectralConfig     spectral;
    @ArgGroup(exclusive = false)
    MecConfig          mec;
  }

  static class KMeansConfig
  {
    @Option(names = { "-K", "--kmeans.clusters" },
        description = "The desired number of clusters.", required = true)
    int numClusters;
  }

  static class XMeansConfig
  {
    @Option(names = { "-X", "--xmeans.clusters" },
        description = "The desired number of clusters.", required = true)
    int numClusters;
  }

  static class GMeansConfig
  {
    @Option(names = { "-G", "--gmeans.clusters" },
        description = "The desired number of clusters.", required = true)
    int numClusters;
  }

  static class DenClueConfig
  {
    @Option(names = { "--denclue.sigma" },
        description = "The density attractor of each observation.",
        required = true)
    double sigma;
    @Option(names = { "--denclue.m" },
        description = "The radius of density attractor.", required = true)
    int    m;
  }

  static class DbScanConfig
  {
    @Option(names = { "--dbscan.points" },
        description = "The minimum number of points required to form a cluster.",
        required = true)
    int    minPoints;
    @Option(names = { "--dbscan.radius" },
        description = "The neighborhood radius.", required = true)
    double radius;
  }

  static class HierarchicalConfig
  {
    @Option(names = { "-R", "--hierarchical" },
        description = "The number of partitions.", required = true)
    int                 partitions;
    @Option(names = { "--linkage" }, required = false,
        description = "A measure of dissimilarity between clusters. "
            + "Possible values: ${COMPLETION-CANDIDATES}")
    private LinkageType linkage = LinkageType.SINGLE;
  }

  static class ClaransConfig
  {
    @Option(names = { "--clarans.distance" }, required = true)
    int distance;
  }

  static class AnnealingConfig
  {
    @Option(names = { "-A", "--annealing.clusters" },
        description = "The desired number of clusters.", required = true)
    int numClusters;
  }

  static class SpectralConfig
  {
    @Option(names = { "--spectral.clusters" },
        description = "The number of clusters.", required = true)
    int    numClusters;
    @Option(names = { "--spectral.sigma" },
        description = "The smooth/width parameter of Gaussian kernel, which "
            + "is a somewhat sensitive parameter. To search for the "
            + "bestsetting, one may pick the value that gives the tightest "
            + "clusters (smallest distortion) in feature space.",
        required = true)
    double sigma;

  }

  static class MecConfig
  {
    @Option(names = { "--mec.clusters" },
        description = "The number of clusters.", required = true)
    int          numClusters;
    @Option(names = { "--mec.distance" },
        description = "The distance.  Possible values: "
            + "${COMPLETION-CANDIDATES}",
        required = true)
    DistanceType distance;
    @Option(names = { "--mec.radius" }, description = "The radius.",
        required = true)
    double       radius;
  }

  @Option(names = { "-x", "--exclude" }, required = false,
      description = "A list of fields to exclude.")
  private @Getter @Setter List<String> excludes;

  @Option(names = { "-t", "--target" }, required = false,
      description = "The target where the cluster information should be "
          + "stored.  Default = /cluster")
  private @Getter @Setter String       target = "/cluster";

  private List<OpData>                 data   = new ArrayList<>();

  public Cluster()
  {
    super("smile-cluster");
  }

  public List<OpData> execute(OpData input) throws OpsException
  {
    data.add(input);
    return OpData.emptyList();
  }

  public List<OpData> close() throws OpsException
  {
    // Calculate field names
    if (data.size() == 0)
    {
      return OpData.emptyList();
    }

    double x[][];

    List<String> fieldNames = (getFeatures() == null
        || getFeatures().size() <= 0) ? JacksonUtil.keys(data.get(0).toJson())
            : getFeatures();

    if (getExcludes() != null && getExcludes().size() > 0)
    {
      fieldNames.removeAll(getExcludes());
    }

    DEBUG("FIELD-NAMES: ", fieldNames);
    x = SmileUtil.x(data, fieldNames);

    int y[];

    if (clusterConfig.kmeans != null)
    {
      KMeans model = KMeans.fit(x, clusterConfig.kmeans.numClusters);
      y = model.y;
    }
    else if (clusterConfig.xmeans != null)
    {
      XMeans model = XMeans.fit(x, clusterConfig.xmeans.numClusters);
      y = model.y;
    }
    else if (clusterConfig.gmeans != null)
    {
      GMeans model = GMeans.fit(x, clusterConfig.gmeans.numClusters);
      y = model.y;
    }
    else if (clusterConfig.dbscan != null)
    {
      DBSCAN<double[]> model = DBSCAN.fit(x, clusterConfig.dbscan.minPoints,
          clusterConfig.dbscan.radius);
      y = model.y;
    }
    else if (clusterConfig.denclue != null)
    {
      DENCLUE model = DENCLUE.fit(x, clusterConfig.denclue.sigma,
          clusterConfig.denclue.m);
      y = model.y;
    }
    else if (clusterConfig.hierarchical != null)
    {
      HierarchicalClustering model = HierarchicalClustering
          .fit(getLinkage(clusterConfig.hierarchical.linkage, x));
      y = model.partition(clusterConfig.hierarchical.partitions);
    }
    else if (clusterConfig.clarans != null)
    {
      CLARANS<double[]> model = CLARANS.fit(x, MathEx::squaredDistance,
          clusterConfig.clarans.distance);
      y = model.y;
    }
    else if (clusterConfig.annealing != null)
    {
      DeterministicAnnealing model = DeterministicAnnealing.fit(x,
          clusterConfig.annealing.numClusters);
      y = model.y;
    }
    else if (clusterConfig.spectral != null)
    {
      SpectralClustering model = SpectralClustering.fit(x,
          clusterConfig.spectral.numClusters, clusterConfig.spectral.sigma);
      y = model.y;
    }
    else if (clusterConfig.mec != null)
    {
      MEC<double[]> model = MEC.fit(x, getDistance(clusterConfig.mec.distance),
          clusterConfig.mec.numClusters, clusterConfig.mec.radius);
      y = model.y;
    }
    else
    {
      y = new int[0];
    }

    for (int i = 0; i < y.length; i++)
    {
      JacksonUtil.put(getTarget(), data.get(i).getJson(), y[i]);
      // data.get(i).put("CLUSTER", y[i]);
      // System.out.println(JacksonUtil.toPrettyString(data.get(i)));
    }

    return data;
  }

  private Distance getDistance(DistanceType type)
  {
    switch (type)
    {
      case CHEBYSHEV:
      {
        return new ChebyshevDistance();
      }
      case CORRELATION:
      {
        return new CorrelationDistance();
      }
      case EUCLIDIAN:
      {
        return new EuclideanDistance();
      }
      case HAMMING:
      {
        return new HammingDistance();
      }
      case JACCARD:
      {
        return new JaccardDistance<>();
      }
      case MANHATTAN:
      {
        return new ManhattanDistance();
      }
      case SPARSE_CHEBYSHEV:
      {
        return new SparseChebyshevDistance();
      }
      case SPARSE_EUCLIDIAN:
      {
        return new SparseEuclideanDistance();
      }
      case SPARSE_MANHATTAN:
      {
        return new SparseManhattanDistance();
      }
      case JENSEN_SHANNON:
      {
        return new JensenShannonDistance();
      }
      // case SPARSE_MINKOWSKI:
      // case LEE:
      // case MAHALANOBIS:
      // case DYNAMIC_TIMEWARP:
      // case MINKOWSKI:
      default:
      {
        return new EuclideanDistance();
      }
    }
  }

  private Linkage getLinkage(LinkageType ltype, double x[][])
  {
    switch (ltype)
    {
      case SINGLE:
      {
        return SingleLinkage.of(x);
      }
      case COMPLETE:
      {
        return CompleteLinkage.of(x);
      }
      case UPGMA:
      {
        return UPGMALinkage.of(x);
      }
      case UPGMC:
      {
        return UPGMCLinkage.of(x);
      }
      case WARD:
      {
        return WardLinkage.of(x);
      }
      case WPGMA:
      {
        return WPGMALinkage.of(x);
      }
      case WPGMC:
      {
        return WPGMCLinkage.of(x);
      }
      default:
      {
        return SingleLinkage.of(x);
      }
    }
  }

  public static void main(String args[]) throws OpsException
  {
    OpCLI.cli(new Cluster(), args);
  }
}

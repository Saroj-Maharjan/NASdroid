package com.nasdroid.dashboard.logic.dataloading.network

import com.boswelja.capacity.Capacity.Companion.kilobytes
import com.boswelja.capacity.CapacityUnit
import com.nasdroid.api.v2.exception.HttpNotOkException
import com.nasdroid.api.v2.reporting.ReportingV2Api
import com.nasdroid.api.v2.reporting.RequestedGraph
import kotlinx.datetime.Clock
import kotlin.time.Duration.Companion.seconds

/**
 * Retrieves network utilisation data for the system. See [invoke] for details.
 */
class GetNetworkUsageData(
    private val reportingV2Api: ReportingV2Api
) {

    /**
     * Returns a [Result] that contains either [NetworkUsageData], or an exception if the request
     * failed.
     */
    suspend operator fun invoke(adapters: List<String>): Result<NetworkUsageData> {
        return try {
            if (adapters.isEmpty()) return Result.success(NetworkUsageData(emptyList()))
            val now = Clock.System.now()
            val adapterGraphs = reportingV2Api.getGraphData(
                graphs = adapters.map { RequestedGraph(INTERFACE_GRAPH_NAME, it) },
                start = now - 20.seconds,
                end = now,
            )
            val adapterUtilisations = adapterGraphs.map { graph ->
                val data = (graph.data.filter { !it.contains(null) }.map { it.requireNoNulls() }).last()
                val receivedIndex = graph.legend.indexOf("received")
                val sentIndex = graph.legend.indexOf("sent")
                NetworkUsageData.AdapterUtilisation(
                    name = requireNotNull(graph.identifier),
                    // TODO These aren't technically the right unit names
                    receivedBits = data[receivedIndex].kilobytes.toLong(CapacityUnit.BYTE),
                    sentBits = data[sentIndex].kilobytes.toLong(CapacityUnit.BYTE)
                )
            }
            Result.success(
                NetworkUsageData(
                    adapterUtilisation = adapterUtilisations
                )
            )
        } catch (e: HttpNotOkException) {
            Result.failure(e)
        }
    }

    companion object {
        private const val INTERFACE_GRAPH_NAME = "interface"
    }
}

/**
 * Describes the state of network utilisation on the system.
 *
 * @property adapterUtilisation The utilisation of individual network adapters.
 */
data class NetworkUsageData(
    val adapterUtilisation: List<AdapterUtilisation>
) {
    /**
     * Describes the utilisation of a single network adapter.
     *
     * @property name The unique adapter name.
     * @property receivedBits The number of bits the adapter has received.
     * @property sentBits The number of bits the adapter has sent.
     */
    data class AdapterUtilisation(
        val name: String,
        val receivedBits: Long,
        val sentBits: Long,
    )
}

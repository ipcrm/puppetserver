(ns puppetlabs.services.puppet-profiler.puppet-profiler-service
  (:require [puppetlabs.trapperkeeper.core :as trapperkeeper]
            [puppetlabs.services.puppet-profiler.puppet-profiler-core :as puppet-profiler-core]
            [puppetlabs.services.protocols.puppet-profiler :as profiler]
            [puppetlabs.trapperkeeper.services :as tk-services]
            [puppetlabs.trapperkeeper.services.status.status-core :as status-core]))

(trapperkeeper/defservice puppet-profiler-service
                          profiler/PuppetProfilerService
                          [[:MetricsService get-metrics-registry get-server-id]
                           [:ConfigService get-in-config]
                           [:StatusService register-status]]

  (init [this context]
    (let [context (puppet-profiler-core/initialize
                    (get-in-config [:profiler] {})
                    (get-server-id)
                    (get-metrics-registry :puppetserver))]
      (register-status
        "puppet-profiler"
        (status-core/get-artifact-version "puppetlabs" "puppetserver")
        1
        (partial puppet-profiler-core/v1-status (:profiler context)))
      context))

  (get-profiler
    [this]
    (:profiler (tk-services/service-context this))))

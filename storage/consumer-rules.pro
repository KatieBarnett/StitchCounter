
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLitee {
    <fields>;
}

-dontwarn dev.veryniche.stitchcounter.storage.ProjectsDataSource
-dontwarn dev.veryniche.stitchcounter.storage.ProjectsRepository
-dontwarn dev.veryniche.stitchcounter.storage.ProjectsSerializer
-dontwarn dev.veryniche.stitchcounter.storage.di.DataStoreModule_ProvidesProjectsStoreFactory
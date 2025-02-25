param location string = resourceGroup().location
param tags object = {}


param databaseName string = ''
param keyVaultName string

// Because databaseName is optional in main.bicep, we make sure the database name is set here.
var defaultDatabaseName = 'Todo'
var actualDatabaseName = !empty(databaseName) ? databaseName : defaultDatabaseName


param name string

param psqlAdminName string
@secure()
param psqlAdminPassword string

param dbUrlKey string = 'SPRING-DATASOURCE-URL'
param dbUserKey string = 'SPRING-DATASOURCE-USERNAME'
@secure()
param dbPasswordKey string = 'SPRING-DATASOURCE-PASSWORD'

module psql '../core/database/postgresql/flexibleserver.bicep' = {
  name: 'flexible-postgresql'
  params: {
    name: name
    sku: {
      name: 'Standard_D4s_v3'
      tier: 'GeneralPurpose'
    }
    version: '14'
    administratorLogin: psqlAdminName
    administratorLoginPassword: psqlAdminPassword
    storage: {
      storageSizeGB: 32
    }
    databaseNames: [ actualDatabaseName ]
    location: location
    tags: tags
    allowAzureIPsFirewall: true
    allowAllIPsFirewall: true
  }
}

resource psqlConnectionString 'Microsoft.KeyVault/vaults/secrets@2022-07-01' = {
  parent: keyVault
  name: dbUrlKey
  properties: {
    value: 'jdbc:postgresql://${name}.postgres.database.azure.com:5432/${actualDatabaseName}'
  }
}

resource psqlUser 'Microsoft.KeyVault/vaults/secrets@2022-07-01' = {
  parent: keyVault
  name: dbUserKey
  properties: {
    value: psqlAdminName
  }
}

resource psqlPassword 'Microsoft.KeyVault/vaults/secrets@2022-07-01' = {
  parent: keyVault
  name: dbPasswordKey
  properties: {
    value: psqlAdminPassword
  }
}

resource keyVault 'Microsoft.KeyVault/vaults@2022-07-01' existing = {
  name: keyVaultName
}

output psql_domain_name string = psql.outputs.POSTGRES_DOMAIN_NAME


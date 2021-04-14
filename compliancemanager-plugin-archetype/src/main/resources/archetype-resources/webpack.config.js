const PluginWebpackConfig = require('compliancemanager-web-plugin').PluginWebpackConfig;
const loadBuildConfig = require('compliancemanager-web-plugin').loadBuildConfig;
const path = require('path');

// Remember to use the same name here and in `getUniqueId()` in the java MetaData class
module.exports = new PluginWebpackConfig('${package}.${pluginClassName}Plugin', loadBuildConfig(path.resolve(__dirname, './build.config')), {
  // Here goes your additional webpack configuration.
});

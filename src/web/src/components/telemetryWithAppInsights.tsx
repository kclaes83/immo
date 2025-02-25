import { ComponentClass, ComponentType } from "react";
import { withAITracking } from "@microsoft/applicationinsights-react-js";
import { reactPlugin } from "../services/telemetryService";

const withApplicationInsights = (
  component: ComponentType<unknown>,
  componentName: string,
): ComponentClass<ComponentType<unknown>, unknown> =>
  withAITracking<typeof component>(reactPlugin, component, componentName);

export default withApplicationInsights;

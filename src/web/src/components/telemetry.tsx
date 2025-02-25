import { FC, PropsWithChildren, ReactElement, useEffect } from "react";
import {
  getApplicationInsights,
  reactPlugin,
} from "../services/telemetryService";
import { TelemetryProvider } from "./telemetryContext";

type TelemetryProps = PropsWithChildren<unknown>;

const Telemetry: FC<TelemetryProps> = (props: TelemetryProps): ReactElement => {
  useEffect(() => {
    getApplicationInsights();
  }, []);

  return (
    <TelemetryProvider value={reactPlugin}>{props.children}</TelemetryProvider>
  );
};

export default Telemetry;

-- создаём таблицу scenarios
CREATE TABLE IF NOT EXISTS scenarios (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    hub_id VARCHAR,
    name VARCHAR,
    UNIQUE(hub_id, name)
);

-- создаём таблицу sensors
CREATE TABLE IF NOT EXISTS sensors (
    id VARCHAR PRIMARY KEY,
    hub_id VARCHAR
);

-- создаём таблицу conditions
CREATE TABLE IF NOT EXISTS conditions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sensor_id VARCHAR REFERENCES sensors(id),
    type VARCHAR,
    operation VARCHAR,
    value INTEGER
);

-- создаём таблицу actions
CREATE TABLE IF NOT EXISTS actions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sensor_id VARCHAR REFERENCES sensors(id),
    type VARCHAR,
    value INTEGER
);

-- создаём таблицу scenario_conditions, связывающую сценарий, датчик и условие активации сценария
CREATE TABLE IF NOT EXISTS scenario_conditions (
    scenario_id BIGINT REFERENCES scenarios(id),
    condition_id BIGINT REFERENCES conditions(id),
    PRIMARY KEY (scenario_id, condition_id)
);

-- создаём таблицу scenario_actions, связывающую сценарий, датчик и действие, которое нужно выполнить при активации сценария
CREATE TABLE IF NOT EXISTS scenario_actions (
    scenario_id BIGINT REFERENCES scenarios(id),
    action_id BIGINT REFERENCES actions(id),
    PRIMARY KEY (scenario_id, action_id)
);

-- проверка соответствия hub_id для actions
CREATE OR REPLACE FUNCTION check_action_hub_id()
RETURNS TRIGGER AS
$$
BEGIN
    IF (SELECT hub_id FROM scenarios WHERE id = NEW.scenario_id) <>
       (SELECT s.hub_id
          FROM actions a
          JOIN sensors s ON s.id = a.sensor_id
         WHERE a.id = NEW.action_id)
    THEN
        RAISE EXCEPTION 'Hub IDs do not match for scenario_id % and action_id %',
                        NEW.scenario_id, NEW.action_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- проверка соответствия hub_id для conditions
CREATE OR REPLACE FUNCTION check_condition_hub_id()
RETURNS TRIGGER AS
$$
BEGIN
    IF (SELECT hub_id FROM scenarios WHERE id = NEW.scenario_id) <>
       (SELECT s.hub_id
          FROM conditions c
          JOIN sensors s ON s.id = c.sensor_id
         WHERE c.id = NEW.condition_id)
    THEN
        RAISE EXCEPTION 'Hub IDs do not match for scenario_id % and condition_id %',
                        NEW.scenario_id, NEW.condition_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- создаём триггер, проверяющий, что «условие» связывает корректные сценарий и датчик
CREATE OR REPLACE TRIGGER tr_bi_scenario_conditions_hub_id_check
BEFORE INSERT ON scenario_conditions
FOR EACH ROW
EXECUTE FUNCTION check_condition_hub_id();

-- создаём триггер, проверяющий, что «действие» связывает корректные сценарий и датчик
CREATE OR REPLACE TRIGGER tr_bi_scenario_actions_hub_id_check
BEFORE INSERT ON scenario_actions
FOR EACH ROW
EXECUTE FUNCTION check_action_hub_id();